package vn.kyler.job_hunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.Role;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResUserDTO;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.UserRepository;
import vn.kyler.job_hunter.service.exception.ExistsException;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) throws ExistsException, NotFoundException {
        boolean existsEmail = this.userRepository.existsByEmail(user.getEmail());
        if (existsEmail) {
            throw new ExistsException("Email " + user.getEmail() + " already exists");
        }

        if (user.getCompany() != null) {
            Company company = this.companyService.handleGetCompany(user.getCompany().getId());
            user.setCompany(company);
        }

        if (user.getRole() != null) {
            Role role = this.roleService.handleGetRole(user.getRole().getId());
            user.setRole(role);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) throws NotFoundException {
        if (!this.userRepository.existsById(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        this.userRepository.deleteById(id);
    }

    public User handleGetUser(long id) throws NotFoundException {
        Optional<User> user = this.userRepository.findById(id);
        if (!user.isPresent()) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return user.get();
    }

    public User handleGetUserByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        return user.isPresent() ? user.get() : null;
    }


    public ResultPaginationDTO handleGetAllUser(Specification<User> specification, Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(specification, pageable);
        List<ResUserDTO> userDTOs = userPage.getContent().stream()
                .map(user -> {
                    ResUserDTO userDTO = new ResUserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setName(user.getName());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setAge(user.getAge());
                    userDTO.setGender(user.getGender());
                    userDTO.setAddress(user.getAddress());
                    userDTO.setCreatedAt(user.getCreatedAt());
                    userDTO.setUpdatedAt(user.getUpdatedAt());
                    userDTO.setCreatedBy(user.getCreatedBy());
                    userDTO.setUpdatedBy(user.getUpdatedBy());

                    if (user.getCompany() != null) {
                        ResUserDTO.Company company = new ResUserDTO.Company();
                        company.setId(user.getCompany().getId());
                        company.setName(user.getCompany().getName());
                        userDTO.setCompany(company);
                    }

                    if (user.getRole() != null) {
                        ResUserDTO.Role role = new ResUserDTO.Role();
                        role.setId(user.getRole().getId());
                        role.setName(user.getRole().getName());
                        userDTO.setRole(role);
                    }

                    return userDTO;
                })
                .collect(Collectors.toList());
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(userPage.getNumber() + 1);
        meta.setPageSize(userPage.getSize());
        meta.setTotalPages(userPage.getTotalPages());
        meta.setTotalElements(userPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(userDTOs);
        return resultPaginationDTO;
    }

    public User handleUpdateUser(User user) throws NotFoundException {
        Optional<User> userOptional = this.userRepository.findById(user.getId());
        if (!userOptional.isPresent()) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
        User userToUpdate = userOptional.get();
        userToUpdate.setName(user.getName());
        // userToUpdate.setEmail(user.getEmail());
        userToUpdate.setAge(user.getAge());
        userToUpdate.setGender(user.getGender());
        userToUpdate.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            Company company = this.companyService.handleGetCompany(user.getCompany().getId());
            userToUpdate.setCompany(company);
        } else userToUpdate.setCompany(null);

        if (user.getRole() != null) {
            Role role = this.roleService.handleGetRole(user.getRole().getId());
            userToUpdate.setRole(role);
        }else  userToUpdate.setRole(null);
        return this.userRepository.save(userToUpdate);
    }

    public ResUserDTO handleConvertToUserDTO(User user) {
        ResUserDTO userDTO = new ResUserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAge(user.getAge());
        userDTO.setGender(user.getGender());
        userDTO.setAddress(user.getAddress());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setUpdatedBy(user.getUpdatedBy());

        if (user.getCompany() != null) {
            ResUserDTO.Company company = new ResUserDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            userDTO.setCompany(company);
        }

        if (user.getRole() != null) {
            ResUserDTO.Role role = new ResUserDTO.Role();
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            userDTO.setRole(role);
        }
        return userDTO;
    }

    public void handleUpdateUserToken(String email, String token) {
        User user = this.handleGetUserByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User handleGetUserByRefreshTokenAndEmail(String refreshToken, String email) {
        Optional<User> userOptional = this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
        if (!userOptional.isPresent()) {
            return null;
        }
        return userOptional.get();
    }
}

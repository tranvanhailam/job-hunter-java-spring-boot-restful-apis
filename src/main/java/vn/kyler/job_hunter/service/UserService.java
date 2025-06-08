package vn.kyler.job_hunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResUserDTO;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.UserRepository;
import vn.kyler.job_hunter.service.exception.EmailExistsException;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) throws EmailExistsException {
        boolean existsEmail = this.userRepository.existsByEmail(user.getEmail());
        if (existsEmail) {
            throw new EmailExistsException("Email " + user.getEmail() + " already exists");
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

    public List<User> handleGetAllUser() {
        return this.userRepository.findAll();
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
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            userToUpdate.setName(user.getName());
            // userToUpdate.setEmail(user.getEmail());
            userToUpdate.setAge(user.getAge());
            userToUpdate.setGender(user.getGender());
            userToUpdate.setAddress(user.getAddress());

            return this.userRepository.save(userToUpdate);
        } else {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
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

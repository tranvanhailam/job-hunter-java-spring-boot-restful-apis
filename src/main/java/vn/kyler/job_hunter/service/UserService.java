package vn.kyler.job_hunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.dto.Meta;
import vn.kyler.job_hunter.domain.dto.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUser(long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.isPresent() ? user.get() : null;
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
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPageNumber(userPage.getNumber() + 1);
        meta.setPageSize(userPage.getSize());
        meta.setTotalPages(userPage.getTotalPages());
        meta.setTotalElements(userPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(userPage.getContent());
        return resultPaginationDTO;
    }

    public User handleUpdateUser(User user) {
        Optional<User> userOptional = this.userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            userToUpdate.setName(user.getName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());
            return this.userRepository.save(userToUpdate);
        } else {
            return null;
        }
    }
}

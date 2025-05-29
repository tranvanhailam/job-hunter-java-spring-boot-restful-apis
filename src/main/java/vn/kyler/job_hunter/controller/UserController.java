package vn.kyler.job_hunter.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.dto.ResultPaginationDTO;
import vn.kyler.job_hunter.service.IdInvalidException;
import vn.kyler.job_hunter.service.UserService;
import vn.kyler.job_hunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userCreated = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id <= 0) {
            throw new IdInvalidException("Id must be greater than 0!");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete user successfully!");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User user = this.userService.handleGetUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            Pageable pageable,
            @Filter Specification<User> specification) {
        ResultPaginationDTO resultPaginationDTO = this.userService.handleGetAllUser(specification,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User userUpdated = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdated);
    }

}

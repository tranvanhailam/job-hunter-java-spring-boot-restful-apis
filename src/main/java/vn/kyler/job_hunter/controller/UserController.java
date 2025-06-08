package vn.kyler.job_hunter.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.kyler.job_hunter.domain.Company;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResUserDTO;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.UserService;
import vn.kyler.job_hunter.service.exception.EmailExistsException;
import vn.kyler.job_hunter.service.exception.IdInvalidException;
import vn.kyler.job_hunter.service.exception.NotFoundException;
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
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) throws EmailExistsException {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userCreated = this.userService.handleCreateUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleConvertToUserDTO(userCreated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) throws NotFoundException {
        this.userService.handleDeleteUser(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete user successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id) throws NotFoundException {
        User user = this.userService.handleGetUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleConvertToUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            Pageable pageable,
            @Filter Specification<User> specification) {
        ResultPaginationDTO resultPaginationDTO = this.userService.handleGetAllUser(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody User user) throws NotFoundException {
        User userUpdated = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleConvertToUserDTO(userUpdated));
    }
}

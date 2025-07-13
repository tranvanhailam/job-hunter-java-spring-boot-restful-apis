package vn.kyler.job_hunter.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.Role;
import vn.kyler.job_hunter.domain.response.ResRoleDTO;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.RoleService;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@RestController
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<ResRoleDTO> createRole(@RequestBody Role role) {
        Role roleCreated = this.roleService.handleCreateRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleConvertToRoleDTO(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<ResRoleDTO> updateRole(@RequestBody Role role) throws NotFoundException {
        Role roleUpdated = this.roleService.handleUpdateRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.handleConvertToRoleDTO(roleUpdated));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<ResRoleDTO> getRole(@PathVariable("id") long id) throws NotFoundException {
        Role role = this.roleService.handleGetRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.handleConvertToRoleDTO(role));
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllRole(Pageable pageable, @Filter Specification<Role> specification) throws NotFoundException {
        ResultPaginationDTO resultPaginationDTO = this.roleService.handleGetAllRole(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable("id") long id) throws NotFoundException {
        this.roleService.handleDeleteRole(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete role successfully");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}

package vn.kyler.job_hunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.kyler.job_hunter.domain.Permission;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.service.PermissionService;
import vn.kyler.job_hunter.service.exception.NotFoundException;
import vn.kyler.job_hunter.util.annotation.ApiMessage;

import java.util.List;

@RestController
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        Permission permissionCreated = this.permissionService.handleCreatePermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionCreated);
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws NotFoundException {
        Permission permissionUpdated = this.permissionService.handleUpdatePermission(permission);
        return ResponseEntity.status(HttpStatus.OK).body(permissionUpdated);
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable long id) throws NotFoundException {
        this.permissionService.handleDeletePermission(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete permission successfully");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @GetMapping("/permissions/{id}")
    public ResponseEntity<Permission> getPermission(@PathVariable long id) throws NotFoundException {
        Permission permission = this.permissionService.handleGetPermissionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(permission);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(Pageable pageable,
                                                                 @Filter Specification<Permission> specification) {
        ResultPaginationDTO resultPaginationDTO = this.permissionService.handleGetAllPermission(specification,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }


}

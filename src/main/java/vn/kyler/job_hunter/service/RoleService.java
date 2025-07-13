package vn.kyler.job_hunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.Role;
import vn.kyler.job_hunter.domain.response.ResRoleDTO;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.RoleRepository;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public Role handleCreateRole(Role role) {
        if (role.getPermissions() != null) {
            role.setPermissions(
                    role.getPermissions().stream().map(permission -> {
                        try {
                            return this.permissionService.handleGetPermissionById(permission.getId());
                        } catch (NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList())
            );
        }
        return this.roleRepository.save(role);
    }

    public Role handleGetRole(long id) throws NotFoundException {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (!roleOptional.isPresent()) {
            throw new NotFoundException("Role with id " + id + " not found");
        }
        return roleOptional.get();
    }

    public void handleDeleteRole(long id) throws NotFoundException {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (!roleOptional.isPresent()) {
            throw new NotFoundException("Role with id " + id + " not found");
        }
        this.roleRepository.deleteById(id);
    }

    public Role handleUpdateRole(Role role) throws NotFoundException {
        Optional<Role> roleOptional = this.roleRepository.findById(role.getId());
        if (!roleOptional.isPresent()) {
            throw new NotFoundException("Role with id " + role.getId() + " not found");
        }

        Role roleToUpdate = roleOptional.get();
        roleToUpdate.setName(role.getName());
        roleToUpdate.setDescription(role.getDescription());
        roleToUpdate.setActive(role.isActive());

        if (role.getPermissions() != null) {
            roleToUpdate.setPermissions(
                    role.getPermissions().stream().map(permission -> {
                        try {
                            return this.permissionService.handleGetPermissionById(permission.getId());
                        } catch (NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList())
            );
        } else roleToUpdate.setPermissions(null);

        return this.roleRepository.save(roleToUpdate);
    }

    public ResultPaginationDTO handleGetAllRole(Specification<Role> specification, Pageable pageable) {
        Page<Role> jobPage = this.roleRepository.findAll(specification, pageable);

        List<ResRoleDTO> roleDTOS = jobPage.getContent().stream().map(role -> {
            ResRoleDTO roleDTO = new ResRoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            roleDTO.setActive(role.isActive());
            roleDTO.setCreatedAt(role.getCreatedAt());
            roleDTO.setUpdatedAt(role.getUpdatedAt());
            roleDTO.setCreatedBy(role.getCreatedBy());
            roleDTO.setUpdatedBy(role.getUpdatedBy());


            if (role.getPermissions() != null) {
                roleDTO.setPermissions(
                        role.getPermissions().stream().map(permission -> {
                            ResRoleDTO.Permission permissionDTO = new ResRoleDTO.Permission();
                            permissionDTO.setId(permission.getId());
                            permissionDTO.setName(permission.getName());
                            return permissionDTO;
                        }).collect(Collectors.toList())
                );
            }
            return roleDTO;
        }).collect(Collectors.toList());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(jobPage.getNumber() + 1);
        meta.setPageSize(jobPage.getSize());
        meta.setTotalPages(jobPage.getTotalPages());
        meta.setTotalElements(jobPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(roleDTOS);
        return resultPaginationDTO;
    }

    public ResRoleDTO handleConvertToRoleDTO(Role role) {
        ResRoleDTO resRoleDTO = new ResRoleDTO();
        resRoleDTO.setId(role.getId());
        resRoleDTO.setName(role.getName());
        resRoleDTO.setDescription(role.getDescription());
        resRoleDTO.setActive(role.isActive());
        resRoleDTO.setCreatedAt(role.getCreatedAt());
        resRoleDTO.setUpdatedAt(role.getUpdatedAt());
        resRoleDTO.setCreatedBy(role.getCreatedBy());
        resRoleDTO.setUpdatedBy(role.getUpdatedBy());

        if (role.getPermissions() != null) {
            resRoleDTO.setPermissions(
                    role.getPermissions().stream().map(permission -> {

                        ResRoleDTO.Permission permissionDTO = new ResRoleDTO.Permission();
                        permissionDTO.setId(permission.getId());
                        permissionDTO.setName(permission.getName());
                        return permissionDTO;
                    }).collect(Collectors.toList())
            );
        }
        return resRoleDTO;
    }
}

package vn.kyler.job_hunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.kyler.job_hunter.domain.Permission;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.response.ResultPaginationDTO;
import vn.kyler.job_hunter.repository.PermissionRepository;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission handleUpdatePermission(Permission permission) throws NotFoundException {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(permission.getId());
        if (permissionOptional.isPresent()) {
            Permission permissionToUpdate = permissionOptional.get();
            permissionToUpdate.setName(permission.getName());
            permissionToUpdate.setApiPath(permission.getApiPath());
            permissionToUpdate.setModule(permission.getModule());
            return this.permissionRepository.save(permissionToUpdate);
        }
        throw new NotFoundException("Permission with id " + permission.getId() + " not found");

    }

    public void handleDeletePermission(long id) throws NotFoundException {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (!permissionOptional.isPresent()) {
            throw new NotFoundException("Permission with id " + id + " not found");
        }
        this.permissionRepository.delete(permissionOptional.get());
    }

    public Permission handleGetPermissionById(long id) throws NotFoundException {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (!permissionOptional.isPresent()) {
            throw new NotFoundException("Permission with id " + id + " not found");
        }
        return permissionOptional.get();
    }

    public ResultPaginationDTO handleGetAllPermission(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> permissionPage = this.permissionRepository.findAll(specification, pageable);
        List<Permission> permissions = permissionPage.getContent();
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPageNumber(permissionPage.getNumber() + 1);
        meta.setPageSize(permissionPage.getSize());
        meta.setTotalPages(permissionPage.getTotalPages());
        meta.setTotalElements(permissionPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(permissions);
        return resultPaginationDTO;
    }
}

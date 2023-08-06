package store.management.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.management.dto.PermissionUpdateRequest;
import store.management.entities.Role;
import store.management.enums.Permission;
import store.management.repositories.RoleRepository;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> fetchAllRoles() {
        return roleRepository.findAll();
    }

    public Role fetchRoleById(Long id) {
        return roleRepository.findById(id).get();
    }

    public Role addOrRemovePermissionInRole(PermissionUpdateRequest permissionUpdateRequest) {
        Role role = roleRepository.findById(permissionUpdateRequest.getRoleId()).get();
        List<Permission> permissionsToAdd = permissionUpdateRequest.getPermissionsToAdd();
        List<Permission> permissionsToRemove = permissionUpdateRequest.getPermissionsToRemove();
        if(nonNull(role)) {
            Set<Permission> permissions = new HashSet<>();
            if (role.getPermissions() != null) {
                permissions.addAll(role.getPermissions());
            }
            if (permissionsToRemove != null) {
                permissions.removeAll(permissionsToRemove);
            }
            if (permissionsToAdd != null) {
                permissions.addAll(permissionsToAdd);
            }
            role.setPermissions(permissions);
            return roleRepository.save(role);
        }
        return null;
    }
}

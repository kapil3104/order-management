package store.management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.management.annotation.RequireAllPermissions;
import store.management.dto.PermissionUpdateRequest;
import store.management.entities.Role;
import store.management.enums.Permission;
import store.management.services.RoleService;
import store.management.util.ResponseEntityBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    @RequireAllPermissions({ Permission.MANAGE_ROLES })
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> addRole(@RequestBody Role role){
        return ResponseEntityBuilder.okResponseEntity(roleService.addRole(role));
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> fetchAllRoles(){
        return ResponseEntityBuilder.okResponseEntity(roleService.fetchAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> fetchRoleById(@PathVariable Long id){
        return ResponseEntityBuilder.okResponseEntity(roleService.fetchRoleById(id));
    }

    @RequireAllPermissions({ Permission.MANAGE_ROLES })
    @PostMapping("/permissions")
    public ResponseEntity<Map<String, Object>> addOrRemovePermissionInRole(@RequestBody PermissionUpdateRequest permissionUpdateRequest){
        return ResponseEntityBuilder.okResponseEntity(roleService.addOrRemovePermissionInRole(permissionUpdateRequest));
    }
}

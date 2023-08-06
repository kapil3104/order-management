package store.management.dto;

import lombok.Data;
import store.management.enums.Permission;

import java.util.List;

@Data
public class PermissionUpdateRequest {
    private long roleId;
    private List<Permission> permissionsToAdd;
    private List<Permission> permissionsToRemove;
}

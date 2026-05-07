package model;

import java.util.HashSet;
import java.util.Set;

public class RolePermission {
    private static final java.util.Map<UserRole, Set<Permission>> ROLE_PERMISSIONS = new java.util.HashMap<>();
    
    static {
        // TA permissions
        Set<Permission> taPermissions = new HashSet<>();
        taPermissions.add(Permission.TA_VIEW_PROFILE);
        taPermissions.add(Permission.TA_EDIT_PROFILE);
        taPermissions.add(Permission.TA_UPLOAD_RESUME);
        taPermissions.add(Permission.TA_APPLY_JOB);
        taPermissions.add(Permission.TA_VIEW_APPLICATIONS);
        ROLE_PERMISSIONS.put(UserRole.TA, taPermissions);
        
        // MO permissions
        Set<Permission> moPermissions = new HashSet<>();
        moPermissions.add(Permission.MO_CREATE_JOB);
        moPermissions.add(Permission.MO_VIEW_MY_JOBS);
        moPermissions.add(Permission.MO_REVIEW_APPLICATIONS);
        moPermissions.add(Permission.MO_EXPORT_DATA);
        ROLE_PERMISSIONS.put(UserRole.MO, moPermissions);
        
        // Admin permissions
        Set<Permission> adminPermissions = new HashSet<>();
        adminPermissions.add(Permission.ADMIN_MANAGE_USERS);
        adminPermissions.add(Permission.ADMIN_APPROVE_JOBS);
        adminPermissions.add(Permission.ADMIN_VIEW_LOGS);
        adminPermissions.add(Permission.ADMIN_MANAGE_SYSTEM);
        // Admin also has all TA and MO permissions
        adminPermissions.addAll(taPermissions);
        adminPermissions.addAll(moPermissions);
        ROLE_PERMISSIONS.put(UserRole.ADMIN, adminPermissions);
    }
    
    public static Set<Permission> getPermissionsByRole(UserRole role) {
        return ROLE_PERMISSIONS.getOrDefault(role, new HashSet<>());
    }
    
    public static boolean hasPermission(UserRole role, Permission permission) {
        Set<Permission> permissions = getPermissionsByRole(role);
        return permissions.contains(permission);
    }
}
package service;

import model.Permission;
import model.RolePermission;
import model.User;
import model.UserRole;

public class SecurityService {
    
    public static boolean hasPermission(User user, Permission permission) {
        if (user == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        return RolePermission.hasPermission(role, permission);
    }
    
    public static boolean hasAnyPermission(User user, Permission[] permissions) {
        if (user == null || permissions == null || permissions.length == 0) {
            return false;
        }
        
        for (Permission permission : permissions) {
            if (hasPermission(user, permission)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasAllPermissions(User user, Permission[] permissions) {
        if (user == null || permissions == null || permissions.length == 0) {
            return false;
        }
        
        for (Permission permission : permissions) {
            if (!hasPermission(user, permission)) {
                return false;
            }
        }
        return true;
    }
}
package service;

import annotation.PreAuthorize;
import model.Permission;
import model.User;

import java.lang.reflect.Method;

public class PermissionInterceptor {
    
    public static boolean checkPermission(User user, Object target, Method method) {
        // Check if the method has @PreAuthorize annotation
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
            Permission[] requiredPermissions = annotation.value();
            
            return SecurityService.hasAnyPermission(user, requiredPermissions);
        }
        
        // If no annotation, allow access
        return true;
    }
    
    public static void checkPermissionOrThrow(User user, Object target, Method method) throws SecurityException {
        if (!checkPermission(user, target, method)) {
            throw new SecurityException("Permission denied");
        }
    }
}
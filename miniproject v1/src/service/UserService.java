package service;

import model.*;
import annotation.PreAuthorize;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserService {
    private static final long CACHE_TTL_SHORT = 60 * 1000;
    private static final long CACHE_TTL_MEDIUM = 5 * 60 * 1000;

    @PreAuthorize({Permission.ADMIN_MANAGE_USERS})
    public static User register(String username, String password, String email, String phone, model.UserRole role, String department) {
        if (!isValidPassword(password)) {
            return null;
        }

        if (!isValidEmail(email)) {
            return null;
        }

        if (!isValidPhone(phone)) {
            return null;
        }

        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return null;
            }
        }

        String id = UUID.randomUUID().toString();
        User user = null;
        String encodedPassword = PasswordEncoder.encode(password);

        switch (role) {
            case TA:
                user = new TA(id, username, encodedPassword, email, phone);
                break;
            case MO:
                user = new MO(id, username, encodedPassword, email, phone, department);
                break;
            case ADMIN:
                user = new Admin(id, username, encodedPassword, email, phone, model.AdminLevel.NORMAL);
                break;
        }

        if (user != null) {
            users.add(user);
            DataStorage.saveUsers(users);
            IndexService.indexUserUpdate(user);
            CacheService.invalidate(CacheService.pendingTAsKey());
            DataStorage.addLog("REGISTER", username, "User registered: " + user.getUsername());
        }

        return user;
    }

    public static User login(String username, String password) {
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && PasswordEncoder.matches(password, user.getPassword())) {
                if (user.getStatus() == model.UserStatus.LOCKED) {
                    return null;
                }
                user.setLastLoginAt(java.time.LocalDateTime.now().toString());
                DataStorage.saveUsers(users);
                DataStorage.addLog("LOGIN", username, "User logged in: " + user.getUsername());
                return user;
            }
        }
        return null;
    }

    @PreAuthorize({Permission.ADMIN_MANAGE_USERS})
    public static boolean updateUser(User user) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                DataStorage.saveUsers(users);
                IndexService.indexUserUpdate(user);
                if (user instanceof TA) {
                    CacheService.invalidate(CacheService.pendingTAsKey());
                }
                DataStorage.addLog("UPDATE_USER", user.getUsername(), "User updated: " + user.getUsername());
                return true;
            }
        }
        return false;
    }

    @PreAuthorize({Permission.TA_VIEW_PROFILE, Permission.ADMIN_MANAGE_USERS})
    public static TA getTAProfile(String taId) {
        String cacheKey = "ta_profile_" + taId;
        return CacheService.getOrCompute(cacheKey, () -> {
            return DataStorage.getUsers().stream()
                    .filter(user -> user instanceof TA && user.getId().equals(taId))
                    .map(user -> (TA) user)
                    .findFirst()
                    .orElse(null);
        }, CACHE_TTL_SHORT);
    }

    @PreAuthorize({Permission.TA_EDIT_PROFILE, Permission.ADMIN_MANAGE_USERS})
    public static boolean updateTAProfile(TA ta) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof TA && users.get(i).getId().equals(ta.getId())) {
                ta.setProfileUpdatedAt(java.time.LocalDateTime.now().toString());
                users.set(i, ta);
                DataStorage.saveUsers(users);
                IndexService.indexUserUpdate(ta);
                CacheService.invalidate("ta_profile_" + ta.getId());
                if (ta.getProfileStatus() == ProfileStatus.PENDING) {
                    CacheService.invalidate(CacheService.pendingTAsKey());
                }
                DataStorage.addLog("UPDATE_TA_PROFILE", ta.getUsername(), "TA profile updated: " + ta.getName());
                return true;
            }
        }
        return false;
    }

    @PreAuthorize({Permission.ADMIN_MANAGE_USERS})
    public static boolean reviewTAProfile(String taId, model.ProfileStatus status, String comment) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof TA && users.get(i).getId().equals(taId)) {
                TA ta = (TA) users.get(i);
                ta.setProfileStatus(status);
                ta.setProfileUpdatedAt(java.time.LocalDateTime.now().toString());
                ta.setProfileReviewComment(comment);
                users.set(i, ta);
                DataStorage.saveUsers(users);
                IndexService.indexUserUpdate(ta);
                CacheService.invalidate("ta_profile_" + taId);
                CacheService.invalidate(CacheService.pendingTAsKey());
                DataStorage.addLog("REVIEW_TA_PROFILE", "admin", "TA profile reviewed: " + ta.getName() + " - " + status);
                return true;
            }
        }
        return false;
    }

    @PreAuthorize({Permission.ADMIN_MANAGE_USERS})
    public static boolean toggleUserStatus(String userId, model.UserStatus status) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                users.get(i).setStatus(status);
                DataStorage.saveUsers(users);
                DataStorage.addLog("TOGGLE_USER_STATUS", "admin", "User status changed: " + users.get(i).getUsername() + " - " + status);
                return true;
            }
        }
        return false;
    }

    @PreAuthorize({Permission.ADMIN_MANAGE_USERS})
    public static List<User> getAllUsers() {
        return DataStorage.getUsers();
    }

    public static PaginationUtil.Page<User> getAllUsersPaged(int page, int size) {
        String cacheKey = "all_users_paged_" + page + "_" + size;
        return CacheService.getOrCompute(cacheKey, () -> {
            return PaginationUtil.paginate(DataStorage.getUsers(), page, size);
        }, CACHE_TTL_SHORT);
    }

    @PreAuthorize({Permission.ADMIN_MANAGE_USERS})
    public static List<User> getUsersByRole(model.UserRole role) {
        String cacheKey = "users_by_role_" + role.name();
        return CacheService.getOrCompute(cacheKey, () -> IndexService.getUsersByRole(role), CACHE_TTL_SHORT);
    }

    public static List<TA> getPendingTAs() {
        return CacheService.getOrCompute(CacheService.pendingTAsKey(), () -> {
            return DataStorage.getUsers().stream()
                    .filter(u -> u instanceof TA)
                    .map(u -> (TA) u)
                    .filter(ta -> ta.getProfileStatus() == ProfileStatus.PENDING)
                    .collect(Collectors.toList());
        }, CACHE_TTL_MEDIUM);
    }

    public static PaginationUtil.Page<TA> getPendingTAsPaged(int page, int size) {
        String cacheKey = "pending_tas_paged_" + page + "_" + size;
        return CacheService.getOrCompute(cacheKey, () -> {
            return PaginationUtil.paginateWithConversion(
                    DataStorage.getUsers().stream()
                            .filter(u -> u instanceof TA)
                            .map(u -> (TA) u)
                            .filter(ta -> ta.getProfileStatus() == ProfileStatus.PENDING)
                            .collect(Collectors.toList()),
                    page, size,
                    ta -> ta
            );
        }, CACHE_TTL_MEDIUM);
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= 8 && Pattern.matches(".*[a-zA-Z].*", password) && Pattern.matches(".*[0-9].*", password);
    }

    private static boolean isValidEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email);
    }

    private static boolean isValidPhone(String phone) {
        return Pattern.matches("^1\\d{10}$", phone);
    }

    public static boolean resetPassword(String email, String newPassword) {
        if (!isValidPassword(newPassword)) {
            return false;
        }

        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setPassword(PasswordEncoder.encode(newPassword));
                DataStorage.saveUsers(users);
                DataStorage.addLog("RESET_PASSWORD", user.getUsername(), "Password reset for user: " + user.getUsername());
                return true;
            }
        }
        return false;
    }
} 

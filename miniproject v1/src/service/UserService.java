package service;

import model.*;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserService {
    public static String validateRegistration(String username, String password, String email, String phone) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();

        if (normalizedUsername.isEmpty()) {
            return "用户名不能为空";
        }
        if (usernameExists(normalizedUsername)) {
            return "注册失败，用户名已存在";
        }
        if (!isValidPassword(password)) {
            return "密码需至少8位，且同时包含字母和数字";
        }
        if (!isValidEmail(normalizedEmail)) {
            return "邮箱格式不正确";
        }
        if (!isValidPhone(normalizedPhone)) {
            return "手机号格式不正确，应为11位且以1开头";
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        if (username == null) {
            return false;
        }

        String normalizedUsername = username.trim();
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getUsername() != null && user.getUsername().trim().equalsIgnoreCase(normalizedUsername)) {
                return true;
            }
        }
        return false;
    }

    // 注册新用户
    public static User register(String username, String password, String email, String phone, model.UserRole role, String department) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();
        String normalizedDepartment = department == null ? "" : department.trim();

        if (validateRegistration(normalizedUsername, password, normalizedEmail, normalizedPhone) != null) {
            return null;
        }

        // 创建新用户
        String id = UUID.randomUUID().toString();
        User user = null;

        switch (role) {
            case TA:
                user = new TA(id, normalizedUsername, password, normalizedEmail, normalizedPhone);
                break;
            case MO:
                user = new MO(id, normalizedUsername, password, normalizedEmail, normalizedPhone, normalizedDepartment);
                break;
            case ADMIN:
                user = new Admin(id, normalizedUsername, password, normalizedEmail, normalizedPhone, model.AdminLevel.NORMAL);
                break;
        }

        if (user != null) {
            List<User> users = DataStorage.getUsers();
            users.add(user);
            DataStorage.saveUsers(users);
            DataStorage.addLog("REGISTER", normalizedUsername, "User registered: " + user.getUsername());
        }

        return user;
    }

    // 用户登录
    public static User login(String username, String password) {
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
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

    public static boolean updateUser(User user) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                DataStorage.saveUsers(users);
                DataStorage.addLog("UPDATE_USER", user.getUsername(), "User updated: " + user.getUsername());
                return true;
            }
        }
        return false;
    }

    public static TA getTAProfile(String taId) {
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user instanceof TA && user.getId().equals(taId)) {
                return (TA) user;
            }
        }
        return null;
    }

    public static boolean updateTAProfile(TA ta) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof TA && users.get(i).getId().equals(ta.getId())) {
                ta.setProfileUpdatedAt(java.time.LocalDateTime.now().toString());
                users.set(i, ta);
                DataStorage.saveUsers(users);
                DataStorage.addLog("UPDATE_TA_PROFILE", ta.getUsername(), "TA profile updated: " + ta.getName());
                return true;
            }
        }
        return false;
    }

    public static boolean reviewTAProfile(String taId, model.ProfileStatus status, String comment) {
        List<User> users = DataStorage.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) instanceof TA && users.get(i).getId().equals(taId)) {
                TA ta = (TA) users.get(i);
                ta.setProfileStatus(status);
                ta.setProfileReviewComment(comment);
                ta.setProfileUpdatedAt(java.time.LocalDateTime.now().toString());
                users.set(i, ta);
                DataStorage.saveUsers(users);
                DataStorage.addLog("REVIEW_TA_PROFILE", "admin", "TA profile reviewed: " + ta.getName() + " - " + status);
                return true;
            }
        }
        return false;
    }

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

    public static List<User> getAllUsers() {
        return DataStorage.getUsers();
    }

    public static List<User> getUsersByRole(model.UserRole role) {
        List<User> users = DataStorage.getUsers();
        List<User> result = new java.util.ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    private static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && Pattern.matches(".*[a-zA-Z].*", password) && Pattern.matches(".*[0-9].*", password);
    }

    private static boolean isValidEmail(String email) {
        return email != null && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email);
    }

    private static boolean isValidPhone(String phone) {
        return phone != null && Pattern.matches("^1\\d{10}$", phone);
    }

    public static boolean resetPassword(String email, String newPassword) {
        if (!isValidPassword(newPassword)) {
            return false;
        }

        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setPassword(newPassword);
                DataStorage.saveUsers(users);
                DataStorage.addLog("RESET_PASSWORD", user.getUsername(), "Password reset for user: " + user.getUsername());
                return true;
            }
        }
        return false;
    }
}

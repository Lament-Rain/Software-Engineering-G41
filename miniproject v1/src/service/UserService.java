package service;

import model.*;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserService {
    // Register a new user
    public static User register(String username, String password, String email, String phone, model.UserRole role, String department) {
        // Verify password complexity
        if (!isValidPassword(password)) {
            return null;
        }

        // Verify email format
        if (!isValidEmail(email)) {
            return null;
        }

        // Verify phone number format
        if (!isValidPhone(phone)) {
            return null;
        }

        // Check whether the username already exists
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return null;
            }
        }

        // Create a new user
        String id = UUID.randomUUID().toString();
        User user = null;

        switch (role) {
            case TA:
                user = new TA(id, username, password, email, phone);
                break;
            case MO:
                user = new MO(id, username, password, email, phone, department);
                break;
            case ADMIN:
                user = new Admin(id, username, password, email, phone, model.AdminLevel.NORMAL);
                break;
        }

        // Save user
        if (user != null) {
            users.add(user);
            DataStorage.saveUsers(users);
            DataStorage.addLog("REGISTER", username, "User registered: " + user.getUsername());
        }

        return user;
    }

    // User login
    public static User login(String username, String password) {
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if (user.getStatus() == model.UserStatus.LOCKED) {
                    return null; // Account is locked
                }
                user.setLastLoginAt(java.time.LocalDateTime.now().toString());
                DataStorage.saveUsers(users);
                DataStorage.addLog("LOGIN", username, "User logged in: " + user.getUsername());
                return user;
            }
        }
        return null; // Incorrect username or password
    }

    // Update user information
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

    // Get TA profile
    public static TA getTAProfile(String taId) {
        List<User> users = DataStorage.getUsers();
        for (User user : users) {
            if (user instanceof TA && user.getId().equals(taId)) {
                return (TA) user;
            }
        }
        return null;
    }

    // Update TA profile
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

    // Review TA profile
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
                DataStorage.addLog("REVIEW_TA_PROFILE", "admin", "TA profile reviewed: " + ta.getName() + " - " + status);
                return true;
            }
        }
        return false;
    }

    // Disable/enable user
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

    // Get all users
    public static List<User> getAllUsers() {
        return DataStorage.getUsers();
    }

    // Get users by specific role
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

    // Helper method: verify password complexity
    private static boolean isValidPassword(String password) {
        return password.length() >= 8 && Pattern.matches(".*[a-zA-Z].*", password) && Pattern.matches(".*[0-9].*", password);
    }

    // Helper method: verify email format
    private static boolean isValidEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email);
    }

    // Helper method: verify phone number format
    private static boolean isValidPhone(String phone) {
        return Pattern.matches("^1\\d{10}$", phone);
    }

    // Password reset feature
    public static boolean resetPassword(String email, String newPassword) {
        // Verify new password complexity
        if (!isValidPassword(newPassword)) {
            return false;
        }

        // Find user
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

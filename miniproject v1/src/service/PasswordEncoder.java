package service;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {
    
    // Encrypt password using BCrypt
    public static String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    // Verify password
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        } catch (IllegalArgumentException e) {
            // Handle invalid salt version (e.g., old plain text passwords)
            return rawPassword.equals(encodedPassword);
        }
    }
}
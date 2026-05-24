package service;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.regex.Pattern;

public class FormValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{3,20}$"
    );

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static ValidationResult validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + " is required");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateMinLength(String value, int minLength, String fieldName) {
        if (value == null || value.length() < minLength) {
            return new ValidationResult(false, fieldName + " must be at least " + minLength + " characters");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            return new ValidationResult(false, fieldName + " must not exceed " + maxLength + " characters");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateRange(String value, int minLength, int maxLength, String fieldName) {
        if (value == null || value.length() < minLength) {
            return new ValidationResult(false, fieldName + " must be at least " + minLength + " characters");
        }
        if (value.length() > maxLength) {
            return new ValidationResult(false, fieldName + " must not exceed " + maxLength + " characters");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Username is required");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return new ValidationResult(false, "Username must be 3-20 characters (letters, numbers, underscore)");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password is required");
        }
        if (password.length() < 8) {
            return new ValidationResult(false, "Password must be at least 8 characters");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ValidationResult(false, "Please enter a valid email address");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateNumber(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + " is required");
        }
        try {
            Double.parseDouble(value);
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, fieldName + " must be a valid number");
        }
    }

    public static ValidationResult validatePositiveNumber(String value, String fieldName) {
        ValidationResult basicValidation = validateNumber(value, fieldName);
        if (!basicValidation.isValid()) {
            return basicValidation;
        }
        try {
            double number = Double.parseDouble(value);
            if (number <= 0) {
                return new ValidationResult(false, fieldName + " must be a positive number");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, fieldName + " must be a valid number");
        }
    }

    public static ValidationResult validateInteger(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + " is required");
        }
        try {
            Integer.parseInt(value);
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, fieldName + " must be a whole number");
        }
    }

    public static ValidationResult validatePositiveInteger(String value, String fieldName) {
        ValidationResult basicValidation = validateInteger(value, fieldName);
        if (!basicValidation.isValid()) {
            return basicValidation;
        }
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                return new ValidationResult(false, fieldName + " must be a positive number");
            }
            return new ValidationResult(true, null);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, fieldName + " must be a valid number");
        }
    }

    public static ValidationResult validateDate(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + " is required");
        }
        if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", value)) {
            return new ValidationResult(false, fieldName + " must be in format YYYY-MM-DD");
        }
        return new ValidationResult(true, null);
    }

    public static ValidationResult validateFutureDate(String value, String fieldName) {
        ValidationResult basicValidation = validateDate(value, fieldName);
        if (!basicValidation.isValid()) {
            return basicValidation;
        }
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(value);
            java.time.LocalDate today = java.time.LocalDate.now();
            if (date.isBefore(today)) {
                return new ValidationResult(false, fieldName + " must be a future date");
            }
            return new ValidationResult(true, null);
        } catch (Exception e) {
            return new ValidationResult(false, fieldName + " must be a valid date");
        }
    }

    public static ValidationResult validateComboBox(Object value, String fieldName) {
        if (value == null) {
            return new ValidationResult(false, "Please select a " + fieldName);
        }
        return new ValidationResult(true, null);
    }

    public static void showError(Control field, Label errorLabel, String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
        if (field != null) {
            field.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("error"), true);
        }
    }

    public static void clearError(Control field, Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
        if (field != null) {
            field.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("error"), false);
        }
    }

    public static void showSuccess(Control field, Label successLabel, String message) {
        if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setVisible(true);
            successLabel.setManaged(true);
        }
        if (field != null) {
            field.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("success"), true);
        }
    }

    public static void clearSuccess(Control field, Label successLabel) {
        if (successLabel != null) {
            successLabel.setText("");
            successLabel.setVisible(false);
            successLabel.setManaged(false);
        }
        if (field != null) {
            field.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("success"), false);
        }
    }

    public static void clearAllStates(Control... fields) {
        for (Control field : fields) {
            if (field != null) {
                field.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("error"), false);
                field.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("success"), false);
            }
        }
    }
}

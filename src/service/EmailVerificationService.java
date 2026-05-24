package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailVerificationService {

    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRY_TIME = 60; // 60 seconds
    private static final Map<String, VerificationCode> verificationCodes = new HashMap<>();
    
    // Email configuration
    private static final String SMTP_SERVER = "smtp.163.com";
    private static final String SMTP_PORT = "465";
    private static final String SENDER_EMAIL = "kk581k5@163.com";
    private static final String SENDER_PASSWORD = "TNnPAD8rzXzr6AmP"; // Actual authorization code

    public static String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public static void sendVerificationCode(String email, String code) {
        // First, print the code to console as a fallback
        System.out.println("Verification code for " + email + ": " + code);
        
        // Then try to send email
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", SMTP_SERVER);
        props.put("mail.smtp.starttls.enable", "true");
        
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("BUPT International School TA Recruitment System - Verification Code");
            message.setText("Your verification code is: " + code + "\n\nThis code will expire in 60 seconds.\n\nPlease enter this code to complete your registration.");
            
            Transport.send(message);
            System.out.println("Verification code sent to " + email + " via email");
        } catch (MessagingException e) {
            // Try with port 587
            try {
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.enable", "false");
                props.put("mail.smtp.starttls.enable", "true");
                
                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });
                
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("BUPT International School TA Recruitment System - Verification Code");
                message.setText("Your verification code is: " + code + "\n\nThis code will expire in 60 seconds.\n\nPlease enter this code to complete your registration.");
                
                Transport.send(message);
                System.out.println("Verification code sent to " + email + " via email (port 587)");
            } catch (MessagingException e2) {
                System.out.println("Email sending failed (using fallback method): " + e2.getMessage());
                System.out.println("Please use the verification code printed in the console: " + code);
            }
        }
        
        // Simulate email sending delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void storeVerificationCode(String email, String code) {
        long expiryTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(CODE_EXPIRY_TIME);
        verificationCodes.put(email, new VerificationCode(code, expiryTime));
    }

    public static boolean verifyCode(String email, String code) {
        VerificationCode verificationCode = verificationCodes.get(email);
        if (verificationCode == null) {
            return false;
        }
        if (System.currentTimeMillis() > verificationCode.getExpiryTime()) {
            verificationCodes.remove(email);
            return false;
        }
        return verificationCode.getCode().equals(code);
    }

    public static boolean isCodeExpired(String email) {
        VerificationCode verificationCode = verificationCodes.get(email);
        if (verificationCode == null) {
            return true;
        }
        return System.currentTimeMillis() > verificationCode.getExpiryTime();
    }

    public static long getCodeRemainingTime(String email) {
        VerificationCode verificationCode = verificationCodes.get(email);
        if (verificationCode == null) {
            return 0;
        }
        long remainingTime = verificationCode.getExpiryTime() - System.currentTimeMillis();
        return Math.max(0, remainingTime / 1000);
    }

    private static class VerificationCode {
        private final String code;
        private final long expiryTime;

        public VerificationCode(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}

package service;

import java.util.Random;

public class CaptchaService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CAPTCHA_LENGTH = 4;

    public static String generateCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return captcha.toString();
    }

    public static boolean validateCaptcha(String input, String expected) {
        return expected != null && expected.equalsIgnoreCase(input);
    }
}

package service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    // 32-byte key for AES-256
    private static final String SECRET_KEY = "BUPT_TA_RECRUITMENT_KEY_12345678";
    
    // Generate secret key from the secret string
    private static SecretKey getSecretKey() throws Exception {
        byte[] keyBytes = SECRET_KEY.getBytes("UTF-8");
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    // Encrypt data
    public static String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }
    
    // Decrypt data
    public static String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }
        
        try {
            // Check if the data is Base64 encoded (encrypted data should be)
            if (!isBase64(encryptedData)) {
                return encryptedData;
            }
            
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            
            // Check if the decoded data length is a multiple of 16 (required for AES)
            if (decodedBytes.length % 16 != 0) {
                return encryptedData;
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedData;
        }
    }
    
    // Check if a string is Base64 encoded
    private static boolean isBase64(String s) {
        try {
            Base64.getDecoder().decode(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
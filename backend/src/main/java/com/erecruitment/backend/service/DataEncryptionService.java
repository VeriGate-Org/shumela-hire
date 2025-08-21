package com.erecruitment.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Data encryption service for sensitive information
 */
@Service
public class DataEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(DataEncryptionService.class);
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    @Value("${encryption.key:dGhpcyBpcyBhIGRlZmF1bHQgZW5jcnlwdGlvbiBrZXkgZm9yIGRldmVsb3BtZW50}")
    private String encryptionKey;

    /**
     * Encrypt sensitive data
     */
    public String encrypt(String plainText) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.error("Encryption failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Decrypt sensitive data
     */
    public String decrypt(String encryptedText) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Decryption failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get secret key for encryption/decryption
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * Generate new encryption key
     */
    public String generateNewKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            logger.error("Key generation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Hash sensitive data (one-way)
     */
    public String hashData(String data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            logger.error("Hashing failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Generate secure random salt
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash data with salt
     */
    public String hashWithSalt(String data, String salt) {
        return hashData(data + salt);
    }

    /**
     * Encrypt PII (Personally Identifiable Information)
     */
    public String encryptPII(String piiData) {
        if (piiData == null || piiData.trim().isEmpty()) {
            return piiData;
        }
        
        String encrypted = encrypt(piiData);
        logger.debug("PII data encrypted successfully");
        return encrypted;
    }

    /**
     * Decrypt PII (Personally Identifiable Information)
     */
    public String decryptPII(String encryptedPII) {
        if (encryptedPII == null || encryptedPII.trim().isEmpty()) {
            return encryptedPII;
        }
        
        String decrypted = decrypt(encryptedPII);
        logger.debug("PII data decrypted successfully");
        return decrypted;
    }

    /**
     * Mask sensitive data for logging
     */
    public String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        
        if (data.contains("@")) {
            // Email masking
            String[] parts = data.split("@");
            if (parts.length == 2) {
                String username = parts[0];
                String domain = parts[1];
                String maskedUsername = username.length() > 2 
                    ? username.substring(0, 2) + "***" 
                    : "***";
                return maskedUsername + "@" + domain;
            }
        }
        
        // General data masking - show first 2 and last 2 characters
        if (data.length() > 4) {
            return data.substring(0, 2) + "***" + data.substring(data.length() - 2);
        }
        
        return "****";
    }

    /**
     * Sanitize data for safe storage/transmission
     */
    public String sanitizeData(String data) {
        if (data == null) return null;
        
        return data
                .replaceAll("<script[^>]*>.*?</script>", "") // Remove script tags
                .replaceAll("<[^>]+>", "") // Remove HTML tags
                .replaceAll("[\\r\\n\\t]+", " ") // Replace newlines/tabs with spaces
                .trim();
    }
}

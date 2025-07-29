package com.example.bankcards.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService {

    @Value("${custom.encryption.algorithm:AES}")
    private String algorithm;

    @Value("${custom.encryption.secret:1234567890123456}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
        log.info("EncryptionService initialized with algorithm: {}", algorithm);
    }

    @Override
    public String encrypt(String plainText) {
        try {
            log.debug("Encrypting text");
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
            log.debug("Encryption successful");
            return encryptedBase64;
        } catch (Exception e) {
            log.error("Error during encryption: {}", e.getMessage(), e);
            throw new RuntimeException("Error during encryption", e);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        try {
            log.debug("Decrypting text");
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decryptedText = new String(decryptedBytes);
            log.debug("Decryption successful");
            return decryptedText;
        } catch (Exception e) {
            log.error("Error during decryption: {}", e.getMessage(), e);
            throw new RuntimeException("Error during decryption", e);
        }
    }
}


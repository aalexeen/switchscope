package net.switchscope.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility to encrypt passwords using the same algorithm as EncryptedStringConverter.
 * Run this to generate encrypted passwords for CSV files.
 */
public class PasswordEncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // Load encryption key from application-local.yaml or fallback to default
    private static final String ENCRYPTION_KEY = loadEncryptionKey();

    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }
        try {
            byte[] keyBytes = getKeyBytes();
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedBytes);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data: " + plainText, e);
        }
    }

    private static String loadEncryptionKey() {
        try {
            // Try to load from application-local.yaml
            String yamlPath = "src/main/resources/application-local.yaml";
            java.io.File yamlFile = new java.io.File(yamlPath);

            if (yamlFile.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(yamlFile.toPath()));
                // Simple YAML parsing for app.encryption.key
                String[] lines = content.split("\n");
                boolean inAppSection = false;
                boolean inEncryptionSection = false;

                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("app:")) {
                        inAppSection = true;
                    } else if (inAppSection && trimmed.startsWith("encryption:")) {
                        inEncryptionSection = true;
                    } else if (inEncryptionSection && trimmed.startsWith("key:")) {
                        String key = trimmed.substring(4).trim();
                        System.out.println("Loaded encryption key from application-local.yaml");
                        return key;
                    } else if (!line.startsWith(" ") && !line.startsWith("\t")) {
                        // Reset if we hit a top-level key
                        inAppSection = false;
                        inEncryptionSection = false;
                    }
                }
            }

            // Fallback to default
            System.out.println("Using default encryption key");
            return "change-me-in-production-32chars";
        } catch (Exception e) {
            System.err.println("Error loading encryption key: " + e.getMessage());
            return "change-me-in-production-32chars";
        }
    }

    private static byte[] getKeyBytes() {
        if (ENCRYPTION_KEY == null || ENCRYPTION_KEY.length() < 16) {
            throw new IllegalStateException("Encryption key must be at least 16 characters");
        }
        // Use first 32 bytes (256 bits) for AES-256
        byte[] keyBytes = new byte[32];
        byte[] sourceBytes = ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(sourceBytes, 0, keyBytes, 0, Math.min(sourceBytes.length, 32));
        return keyBytes;
    }

    public static void main(String[] args) {
        // Encrypt all passwords from CSV files
        String[][] passwords = {
            // Network Switches (51-network-switches.csv)
            {"Csw1@24!", "Core Switch 1"},
            {"Csw2@24!", "Core Switch 2"},
            {"Asw1F@24", "Access Switch Floor 1"},
            {"Ubnt24!s", "Network Closet Switch"},

            // Routers (52-routers.csv)
            {"Rtr@24Mn", "Main Router"},
            {"Edg3@Rtr", "Edge Router"},

            // Access Points (53-access-points.csv)
            {"WifiAp01", "Conference Room AP"},
            {"WifiAp02", "Lobby AP"},
            {"CscAp@24", "Enterprise AP"}
        };

        System.out.println("Encrypted passwords for CSV files:");
        System.out.println("=".repeat(80));

        for (String[] entry : passwords) {
            String password = entry[0];
            String device = entry[1];
            String encrypted = encrypt(password);
            System.out.println("Device: " + device);
            System.out.println("Plain:     " + password);
            System.out.println("Encrypted: '" + encrypted + "'");
            System.out.println();
        }

        System.out.println("=".repeat(80));
        System.out.println("Copy these encrypted values to your CSV files");
    }
}

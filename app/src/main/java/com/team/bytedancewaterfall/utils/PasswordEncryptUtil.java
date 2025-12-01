package com.team.bytedancewaterfall.utils;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 密码加密工具类
 * 使用PBKDF2算法进行安全的密码哈希
 */
public class PasswordEncryptUtil {
    
    // 算法名称
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    // 盐值长度（字节）
    private static final int SALT_LENGTH = 16;
    // 生成密钥的长度（位）
    private static final int KEY_LENGTH = 256;
    // 迭代次数，增加此值可以提高安全性，但会降低性能
    private static final int ITERATIONS = 10000;
    
    /**
     * 生成安全的随机盐值
     * @return 生成的盐值，经过Base64编码的字符串
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }
    
    /**
     * 对密码进行加密
     * @param password 原始密码
     * @return 加密后的密码，格式为"盐值:哈希值"
     * @throws NoSuchAlgorithmException 当指定的算法不可用时抛出
     * @throws InvalidKeySpecException 当密钥规范无效时抛出
     */
    public static String encryptPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 生成盐值
        String salt = generateSalt();
        
        // 创建密钥规范
        KeySpec spec = new PBEKeySpec(password.toCharArray(), 
                Base64.decode(salt, Base64.DEFAULT), 
                ITERATIONS, 
                KEY_LENGTH);
        
        // 获取密钥工厂并生成密钥
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        
        // 返回盐值和哈希值的组合
        return salt + ":" + Base64.encodeToString(hash, Base64.DEFAULT);
    }
    
    /**
     * 验证密码是否与加密后的密码匹配
     * @param originalPassword 原始密码
     * @param encryptedPassword 加密后的密码（格式为"盐值:哈希值"）
     * @return 如果密码匹配返回true，否则返回false
     * @throws NoSuchAlgorithmException 当指定的算法不可用时抛出
     * @throws InvalidKeySpecException 当密钥规范无效时抛出
     */
    public static boolean verifyPassword(String originalPassword, String encryptedPassword) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 分割盐值和哈希值
        String[] parts = encryptedPassword.split(":");
        if (parts.length != 2) {
            return false;
        }
        
        String salt = parts[0];
        String storedHash = parts[1];
        
        // 创建密钥规范
        KeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), 
                Base64.decode(salt, Base64.DEFAULT), 
                ITERATIONS, 
                KEY_LENGTH);
        
        // 获取密钥工厂并生成密钥
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        
        // 比较生成的哈希值和存储的哈希值
        return storedHash.equals(Base64.encodeToString(hash, Base64.DEFAULT));
    }
}
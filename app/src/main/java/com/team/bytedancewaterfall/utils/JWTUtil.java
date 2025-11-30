package com.team.bytedancewaterfall.utils;

import com.team.bytedancewaterfall.data.pojo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

/**
 * JWT工具类，用于生成、验证和刷新JWT令牌
 */
public class JWTUtil {
    // 推荐方式1：使用JWT库提供的安全密钥生成器
    private static final String SECRET_STRING = "the-bytedance-test-feed-show-1111111111111111111111111";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Token有效期（毫秒），这里设置为24小时
    private static final long TOKEN_EXPIRATION = 7*24 * 60 * 60 * 1000;

    // 刷新Token有效期（毫秒），这里设置为7天
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    /**
     * 根据User对象生成JWT令牌
     * @param user 用户对象
     * @return JWT令牌字符串
     */
    public static String generateToken(User user) {
        // 创建Claims，用于存储自定义信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("nickname", user.getNickname());
        claims.put("avatar", user.getAvatar());
        claims.put("email", user.getEmail());
        claims.put("phone", user.getPhone());

        // 生成Token的过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_EXPIRATION);

        // 构建并返回JWT令牌
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // 设置主题
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(expirationDate) // 设置过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 使用密钥签名
                .compact();
    }

    /**
     * 生成刷新令牌
     * @param user 用户对象
     * @return 刷新令牌字符串
     */
    public static String generateRefreshToken(User user) {
        // 刷新令牌只需要包含用户标识信息即可
        Date expirationDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(user.getUsername()) // 设置主题为用户名
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(expirationDate) // 设置过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 使用密钥签名
                .compact();
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌字符串
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            // 解析Token，如果Token无效会抛出异常
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Token无效或过期
            return false;
        }
    }

    /**
     * 从JWT令牌中提取Claims
     * @param token JWT令牌字符串
     * @return Claims对象
     */
    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从JWT令牌中提取用户名
     * @param token JWT令牌字符串
     * @return 用户名
     */
    public static String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    /**
     * 从JWT令牌中提取过期时间
     * @param token JWT令牌字符串
     * @return 过期时间
     */
    public static Date extractExpiration(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration();
    }

    /**
     * 检查JWT令牌是否已过期
     * @param token JWT令牌字符串
     * @return 是否已过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // 发生异常视为已过期
        }
    }

    /**
     * 使用刷新令牌刷新访问令牌
     * @param refreshToken 刷新令牌
     * @param user 用户对象
     * @return 新的访问令牌
     */
    public static String refreshAccessToken(String refreshToken, User user) {
        // 验证刷新令牌是否有效
        if (validateToken(refreshToken)) {
            // 生成新的访问令牌
            return generateToken(user);
        }
        return null; // 刷新令牌无效
    }

    /**
     * 从JWT令牌中获取User对象，存储token中保存的信息
     * @param token JWT令牌字符串
     * @return User对象，如果token无效或解析失败则返回null
     */
    public static User extractUserFromToken(String token) {
        try {
            if (!validateToken(token)) {
                return null;
            }

            Claims claims = extractClaims(token);
            User user = new User();

            // 从Claims中提取用户信息并设置到User对象中
            user.setId((String) claims.get("id"));
            user.setUsername((String) claims.get("username"));
            user.setNickname((String) claims.get("nickname"));
            user.setAvatar((String) claims.get("avatar"));
            user.setEmail((String) claims.get("email"));
            user.setPhone((String) claims.get("phone"));

            return user;
        } catch (Exception e) {
            // 解析失败时返回null
            return null;
        }
    }
}
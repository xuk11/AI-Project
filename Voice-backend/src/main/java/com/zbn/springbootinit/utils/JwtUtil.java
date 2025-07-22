package com.zbn.springbootinit.utils;// JwtUtil.java

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class JwtUtil {

    // 有效期 1 小时
    private static final long EXPIRATION = 3600 * 1000;
    // 密钥（需至少 256 位）
    private static final String SECRET = "xTtLzRjP98Gk5Hf4nD7VwQmZ1XyUcOeIaSbWp0KlN2BvYqCzxM3JnFgEhLk";

    // 生成 Token
    public static String generateToken(Long userId) {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");

        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
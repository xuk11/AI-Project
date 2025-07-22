package com.zbn.springbootinit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// JwtInterceptor.java
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("拦截器触发，请求路径：" + request.getRequestURI()); // 添加此行
        // 1. 获取 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendError(response, "缺少Token");
            return false;
        }
        token = token.substring(7);

        try {
            // 2. 解析 Token
            Claims claims = JwtUtil.parseToken(token);

            // 3. 将用户ID存入请求属性
            Long userId = Long.parseLong(claims.getSubject());
            request.setAttribute("currentUserId", userId);

            return true;
        } catch (ExpiredJwtException e) {
            sendError(response, "Token已过期");
            return false;
        } catch (JwtException e) {
            sendError(response, "Token无效");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(
                "{\"code\": 401, \"message\": \"" + msg + "\"}"
        );
    }
}


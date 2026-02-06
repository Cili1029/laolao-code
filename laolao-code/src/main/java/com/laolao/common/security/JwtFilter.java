package com.laolao.common.security;

import com.laolao.common.context.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Resource
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromCookie(request.getCookies());

        // 如果无 JWT，直接交给Security权限检测
        if (jwt == null || jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 先验证jwt是否有效
            Claims claims = jwtUtil.parseJWT(jwt);

            // 合法但是redis不一致，顶号/异账号登陆
            if (claims == null) {
                // 删除jwt
                ResponseCookie cookie = ResponseCookie.from("jwt", "")
                        .httpOnly(true)  // JS 无法读取，防 XSS
                        .path("/")
                        .maxAge(604800) // 7天
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                return;
            }

            // 有效且一致
            Integer userId = Integer.parseInt(claims.get("userId").toString());
            String username = claims.get("username").toString();
            String role = "ROLE_" + claims.get("role").toString();
            // 存入Security和线程
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // jwt无效
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } finally {
            // 无论请求是否成功，最终都清理线程上下文，避免泄露
            UserContext.removeCurrentId();
        }
    }

    private void handleException(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\": 401, \"msg\": \"" + msg + "\"}");
    }

    public String getJwtFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}

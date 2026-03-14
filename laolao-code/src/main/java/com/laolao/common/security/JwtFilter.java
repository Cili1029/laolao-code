package com.laolao.common.security;

import com.laolao.common.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
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
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromCookie(request.getCookies());

        // 如果无 JWT，直接交给Security权限检测
        if (jwt == null || jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 先验证jwt是否有效
            Claims claims = jwtUtils.parseJWT(jwt);

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
            String name = claims.get("name").toString();
            String role = "ROLE_" + claims.get("role").toString();
            // 创建 MyUserDetail 对象存入Security和线程
            MyUserDetail userDetail = new MyUserDetail(
                    userId,
                    username,
                    name,
                    null, // JWT 场景下不需要密码
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetail, null, Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            // jwt无效
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
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

package com.laolao.common.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Resource
    private JwtFilter jwtFilter;
    @Resource
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // 1. 必须改为无状态，不使用 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/sign-in").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write("{\"code\": 401, \"msg\": \"未登录\"}");
                        })
                )
                // 2. 修改登录逻辑，不再使用默认的重定向，而是返回 HttpOnly Cookie
                .formLogin(form -> form
                        .loginProcessingUrl("/api/user/sign-in")
                        .successHandler((request, response, authentication) -> {
                            // 获取信息，创建jwt对象
                            MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
                            HashMap<String, Object> claims = new HashMap<>();
                            claims.put("userId", userDetail.getUserId());
                            claims.put("username", userDetail.getUsername());
                            claims.put("role", userDetail.getAuthorities().iterator().next().getAuthority());
                            String token = jwtUtil.createJWT(claims);

                            // 创建 HttpOnly Cookie
                            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                                    .httpOnly(true)  // JS 无法读取，防 XSS
                                    .path("/")
                                    .maxAge(3600)
                                    .sameSite("Lax") // 防 CSRF
                                    .build();

                            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write("{\"code\": 200, \"msg\": \"登录成功\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write("{\"code\": 401, \"msg\": \"账号或密码错误\"}");
                        })
                )
                // 3. 将 JWT 过滤器加入过滤链
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 跨域配置保持不变
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
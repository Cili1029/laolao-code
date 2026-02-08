package com.laolao.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laolao.common.result.Result;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/sign-in", "/api/user/sign-up", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(objectMapper.writeValueAsString(Result.error("未登录")));
                        })
                )
                // 登录操作
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
                                    .maxAge(604800) // 7天
                                    .build();

                            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(objectMapper.writeValueAsString(Result.success("登录成功")));
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(objectMapper.writeValueAsString(Result.error("账号或密码错误")));
                        })
                )
                // 将JWT过滤器加入过滤链
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // 推出操作
                .logout(logout -> logout
                        .logoutUrl("/api/user/sign-out")
                        .addLogoutHandler((request, response, authentication) -> {
                            // 清理redis里的token
                            Cookie[] cookies = request.getCookies();
                            String jwt = jwtFilter.getJwtFromCookie(cookies);
                            if (jwt != null) {
                                try {
                                    Claims claims = jwtUtil.parseJWT(jwt);
                                    int userId = Integer.parseInt(claims.get("userId").toString());
                                    stringRedisTemplate.delete("CODE:TOKEN:" + userId);
                                } catch (Exception ignored) {}
                            }
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // 删除jwt
                            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                                    .httpOnly(true)  // JS 无法读取，防 XSS
                                    .path("/")
                                    .maxAge(0) // 立即失效
                                    .build();
                            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                            response.setContentType("application/json;charset=utf-8");
                            response.getWriter().write(objectMapper.writeValueAsString(Result.success("已注销")));
                        }));

        return http.build();
    }

    // 跨域配置
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
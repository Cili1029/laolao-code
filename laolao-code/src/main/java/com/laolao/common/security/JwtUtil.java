package com.laolao.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private String getSecretKey() {
        return jwtProperties.getUserSecretKey();
    }

    private long getTtlMillis() {
        return jwtProperties.getUserTtl();
    }

    /**
     * 生成jwt并且存入Redis
     *
     * @param claims 内容
     * @return jwt令牌
     */
    public String createJWT(Map<String, Object> claims) {
        // 设置过期时间
        Date exp = new Date(System.currentTimeMillis() + getTtlMillis());

        String jwt = Jwts.builder()
                .claims(claims)
                .signWith(Keys.hmacShaKeyFor(getSecretKey().getBytes()))
                .expiration(exp)
                .compact();

        stringRedisTemplate.opsForValue().set("CODE:TOKEN:" + Integer.parseInt(claims.get("userId").toString()),
                jwt, 7, TimeUnit.DAYS);
        return jwt;
    }

    /**
     * jwt解密并验证
     * 调用者需要自行处理异常（jwt无效时）
     *
     * @param jwt jwt令牌
     * @return {userId=1, username=laolao, role=1, exp=1760444574}，空则为顶号
     */
    public Claims parseJWT(String jwt) {
        Claims user = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(getSecretKey().getBytes()))
                .build().parseSignedClaims(jwt).getPayload();

        // 合法，继续验证Redis
        int userId = Integer.parseInt(user.get("userId").toString());
        String redisJwt = stringRedisTemplate.opsForValue().get("CODE:TOKEN:" + userId);
        if (redisJwt != null && redisJwt.equals(jwt)) {
            // 全部合法
            return user;
        } else {
            // 顶号/异账号登陆
            return null;
        }
    }
}

package com.laolao.common.util;

import com.laolao.common.security.MyUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 基于security的工具类
 */
public class SecurityUtils {
    // 判断是否是某个权限
    public static boolean hasAuthority(String authority) {
        final String targetAuthority = "ROLE_" + authority;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> targetAuthority.equals(auth.getAuthority()));
    }

    // 获取用户Id
    public static Integer getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof MyUserDetail) {
            return ((MyUserDetail) principal).getUserId();
        }
        return null;
    }

    // 获取用户完整信息（id， username， name）
    public static MyUserDetail getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof MyUserDetail) {
            return ((MyUserDetail) principal);
        }
        return null;
    }
}

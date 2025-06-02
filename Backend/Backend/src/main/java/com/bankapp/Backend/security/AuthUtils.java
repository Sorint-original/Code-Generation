package com.bankapp.Backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("Unauthorized");
    }
}
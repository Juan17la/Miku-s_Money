package com.mikusmoney.mikusMoney.utils;

import com.mikusmoney.mikusMoney.entity.Miku;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static Miku getAuthenticatedMiku() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        if (authentication.getPrincipal() instanceof Miku) {
            return (Miku) authentication.getPrincipal();
        }
        
        throw new IllegalStateException("Invalid authentication principal");
    }

    public static Long getAuthenticatedMikuId() {
        return getAuthenticatedMiku().getId();
    }
}

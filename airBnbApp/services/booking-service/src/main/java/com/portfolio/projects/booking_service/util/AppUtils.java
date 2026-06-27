package com.portfolio.projects.booking_service.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {

    public static Long getCurrentUserId() {
        try {
            String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Long.valueOf(principal);
        } catch (Exception e) {
            // Fallback for testing or if security context is not fully set up yet
            return 1L;
        }
    }
}

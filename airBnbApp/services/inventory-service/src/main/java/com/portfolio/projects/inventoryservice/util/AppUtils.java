package com.portfolio.projects.inventoryservice.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {

    public static Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

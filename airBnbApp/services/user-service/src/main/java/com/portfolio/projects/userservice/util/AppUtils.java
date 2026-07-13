package com.portfolio.projects.userservice.util;

import com.portfolio.projects.userservice.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

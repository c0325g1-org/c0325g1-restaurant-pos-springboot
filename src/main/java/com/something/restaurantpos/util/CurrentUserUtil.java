package com.something.restaurantpos.util;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.security.EmployeePrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtil {
    public static Employee getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof EmployeePrincipal) {
            return ((EmployeePrincipal) principal).getEmployee();
        }
        return null;
    }
}

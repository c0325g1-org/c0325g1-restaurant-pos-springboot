package com.something.restaurantpos.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RedirectService {
    public String getRedirectUrlByRole(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            return switch (role) {
                case "ROLE_QUẢN_LÝ" -> "redirect:/manager/dashboard";
                case "ROLE_THU_NGÂN" -> "redirect:/cashier/dashboard";
                case "ROLE_PHỤC_VỤ" -> "redirect:/waiter/dashboard";
                case "ROLE_BẾP" -> "redirect:/kitchen/dashboard";
                default -> "redirect:/login";
            };
        }
        return "redirect:/login";
    }
}

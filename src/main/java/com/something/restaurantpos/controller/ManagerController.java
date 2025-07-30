package com.something.restaurantpos.controller;

import com.something.restaurantpos.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        String employeeName = userDetailsService.getEmployeeNameByUsername(username);
        model.addAttribute("employeeName", employeeName);
        return "pages/manager/dashboard";
    }
}

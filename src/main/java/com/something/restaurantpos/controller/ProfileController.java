package com.something.restaurantpos.controller;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private IEmployeeService employeeService;

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProfileData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không có quyền truy cập"));
        }

        String username = authentication.getName();
        Optional<Employee> employeeOpt = employeeService.findByUsername(username);
        
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin nhân viên"));
        }

        Employee employee = employeeOpt.get();
        Map<String, Object> profileData = new HashMap<>();
        
        profileData.put("id", employee.getId());
        profileData.put("name", employee.getName());
        profileData.put("username", employee.getUsername());
        profileData.put("email", employee.getEmail());
        profileData.put("role", employee.getRole().getName());
        profileData.put("createdAt", employee.getCreatedAt());
        profileData.put("updatedAt", employee.getUpdatedAt());
        profileData.put("deleted", employee.isDeleted());
        
        return ResponseEntity.ok(profileData);
    }
} 
package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.repository.IEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm Employee trong database theo username
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Kiểm tra trạng thái kích hoạt
        if (!employee.getEnable()) {
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
        }

        // Xử lý mã hóa mật khẩu nếu chưa được mã hóa
        String password = employee.getPassword();
        if (password != null && !password.startsWith("$2a$")) {
            // Nếu mật khẩu chưa mã hóa, thì mã hóa và lưu lại
            password = passwordEncoder.encode(password);
            employee.setPassword(password);
            employeeRepository.save(employee);
        }

        // Tạo UserDetails object cho Spring Security
        // Sử dụng User.builder() để tạo user với role từ Employee
        return User.builder()
                .username(employee.getUsername())
                .password(password)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getRole().getName())))
                .build();
    }
    public String getEmployeeNameByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        return employee != null ? employee.getName() : username;
    }
} 
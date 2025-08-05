package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.security.EmployeePrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IEmployeeRepository employeeRepository;

    @Lazy // Tránh vòng lặp dependency
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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

        return new EmployeePrincipal(employee); // Trả về Custom Principal
    }

    public String getEmployeeNameByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .map(Employee::getName)
                .orElse(username);
    }
}

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

        // Nếu mật khẩu chưa được mã hóa (không bắt đầu bằng $2a$)
        if (employee.getPassword() != null && !employee.getPassword().startsWith("$2a$")) {
            String encoded = passwordEncoder.encode(employee.getPassword());
            employee.setPassword(encoded);
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

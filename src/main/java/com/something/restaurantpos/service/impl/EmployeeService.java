package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public boolean updatePassword(String username, String currentPassword, String newPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        if (employeeOpt.isEmpty()) {
            return false;
        }

        Employee employee = employeeOpt.get();
        
        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, employee.getPassword())) {
            return false;
        }

        // Cập nhật mật khẩu mới
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);
        return true;
    }
}

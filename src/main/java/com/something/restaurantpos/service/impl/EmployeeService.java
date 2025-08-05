package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.repository.IEmployeeRepository;
import com.something.restaurantpos.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;

    @Override
    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }
}

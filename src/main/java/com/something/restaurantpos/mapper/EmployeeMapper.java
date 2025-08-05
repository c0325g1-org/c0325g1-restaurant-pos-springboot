package com.something.restaurantpos.mapper;

import com.something.restaurantpos.dto.EmployeeDTO;
import com.something.restaurantpos.dto.EmployeeUpdateDTO;
import com.something.restaurantpos.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setUsername(employee.getUsername());
        dto.setEmail(employee.getEmail());
        dto.setEnable(employee.getEnable());
        
        if (employee.getRole() != null) {
            dto.setRoleId(employee.getRole().getId());
            dto.setRoleName(employee.getRole().getName());
        }
        
        return dto;
    }

    public Employee toEntity(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setUsername(dto.getUsername());
        employee.setEmail(dto.getEmail());
        employee.setEnable(dto.getEnable());
        
        return employee;
    }

    public EmployeeUpdateDTO toUpdateDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setUsername(employee.getUsername());
        dto.setEmail(employee.getEmail());
        dto.setEnable(employee.getEnable());
        
        if (employee.getRole() != null) {
            dto.setRoleId(employee.getRole().getId());
            dto.setRoleName(employee.getRole().getName());
        }
        
        return dto;
    }
} 
package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeShift extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private Shift shift;

    private LocalDate shiftDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
}

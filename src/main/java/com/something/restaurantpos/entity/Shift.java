package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shift extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
}

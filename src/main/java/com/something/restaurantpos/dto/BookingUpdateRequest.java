package com.something.restaurantpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateRequest {
    private Integer id;
    private Integer people;
    private LocalDateTime dateTime;
    private String note;
}

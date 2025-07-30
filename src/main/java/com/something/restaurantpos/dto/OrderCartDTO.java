package com.something.restaurantpos.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCartDTO {
    private Integer tableId;
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderCartDTO(Integer tableId) {
        this.tableId = tableId;
    }
}

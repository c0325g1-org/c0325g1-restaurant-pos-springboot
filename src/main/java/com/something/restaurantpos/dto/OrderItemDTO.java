package com.something.restaurantpos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal price;
}
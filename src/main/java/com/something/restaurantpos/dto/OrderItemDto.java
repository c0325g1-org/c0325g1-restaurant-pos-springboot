package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {
    private Integer id;
    private String itemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;

}

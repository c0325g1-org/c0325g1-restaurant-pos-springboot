package com.something.restaurantpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceDto {
    private Integer id;
    private Integer orderId;
    private String tableName;
    private String employeeName;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private List<OrderItemDto> orderItems;  // ✅ Danh sách món
    private Boolean paid;
    private String note;
    // ✅ Ghi chú
}

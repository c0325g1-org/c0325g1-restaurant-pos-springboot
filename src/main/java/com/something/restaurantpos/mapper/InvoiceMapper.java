package com.something.restaurantpos.mapper;

import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.dto.OrderItemDto;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.entity.OrderItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    @Autowired
    private ModelMapper modelMapper;

    public InvoiceDto toDto(Invoice invoice) {
        InvoiceDto dto = modelMapper.map(invoice, InvoiceDto.class);
        dto.setOrderId(invoice.getOrder().getId());
        dto.setTableName(invoice.getOrder().getTable().getName());
        dto.setEmployeeName(invoice.getOrder().getEmployee().getName());
        dto.setOrderTime(invoice.getOrder().getOrderTime());

        List<OrderItemDto> itemDtos = invoice.getOrder().getOrderItems()
                .stream()
                .map(this::mapOrderItem)
                .collect(Collectors.toList());

        dto.setOrderItems(itemDtos);
        return dto;
    }

    private OrderItemDto mapOrderItem(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setItemName(item.getMenuItem().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return dto;
    }
}
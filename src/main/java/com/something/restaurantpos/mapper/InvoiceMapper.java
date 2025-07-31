package com.something.restaurantpos.mapper;

import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.dto.OrderItemDTO;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.service.IOrderItemService;
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

    @Autowired
    private IOrderItemService orderItemService;

    public InvoiceDto toDto(Invoice invoice) {
        InvoiceDto dto = modelMapper.map(invoice, InvoiceDto.class);
        dto.setOrderId(invoice.getOrder().getId());
        dto.setTableName(invoice.getOrder().getTable().getName());
        dto.setEmployeeName(invoice.getOrder().getEmployee().getName());
        dto.setOrderTime(invoice.getOrder().getCreatedAt());

        List<OrderItem> orderItems = orderItemService.findAllByOrderId(invoice.getOrder().getId());
        List<OrderItemDTO> itemDtos = orderItems
                .stream()
                .map(this::mapOrderItem)
                .collect(Collectors.toList());

        dto.setOrderItems(itemDtos);
        return dto;
    }

    private OrderItemDTO mapOrderItem(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setMenuItemName(item.getMenuItem().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
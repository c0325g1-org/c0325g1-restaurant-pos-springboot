package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.OrderItem;
public class StatusViewHelper {

    public String getDisplayStatus(KitchenOrderDTO dto) {
        if (dto.hasStatus(OrderItem.ItemStatus.COOKING)) return "Đang chế biến";
        if (dto.hasStatus(OrderItem.ItemStatus.NEW)) return "Chưa bắt đầu";
        return "Đã sẵn sàng";
    }

    public String getBadgeClass(KitchenOrderDTO dto) {
        return switch (getDisplayStatus(dto)) {
            case "Chưa bắt đầu" -> "bg-secondary text-white";
            case "Đang chế biến" -> "bg-warning text-dark";
            case "Đã sẵn sàng" -> "bg-success text-white";
            default -> "bg-light text-dark";
        };
    }

    public String getCardClass(KitchenOrderDTO dto) {
        return switch (getDisplayStatus(dto)) {
            case "Chưa bắt đầu" -> "bg-secondary text-white";
            case "Đang chế biến" -> "bg-warning text-dark";
            case "Đã sẵn sàng" -> "bg-success text-white";
            default -> "";
        };
    }
}


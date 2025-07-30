package com.something.restaurantpos.util;

import com.something.restaurantpos.dto.OrderCartDTO;
import com.something.restaurantpos.dto.OrderItemDTO;

public class OrderCartUtils {

    public static void addItem(OrderCartDTO cart, OrderItemDTO newItem) {
        for (OrderItemDTO item : cart.getItems()) {
            if (item.getMenuItemId().equals(newItem.getMenuItemId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        cart.getItems().add(newItem);
    }

    public static void removeItem(OrderCartDTO cart, Integer menuItemId) {
        cart.getItems().removeIf(item -> item.getMenuItemId().equals(menuItemId));
    }

    public static void clear(OrderCartDTO cart) {
        cart.getItems().clear();
        cart.setTableId(null);
    }
}


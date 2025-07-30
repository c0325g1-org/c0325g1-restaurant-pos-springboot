package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.OrderCartDTO;
import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface IOrderService {
    Order placeOrder(OrderCartDTO cartDTO, Integer employeeId);

    Optional<Order> findLastedOpenOrderByTableId(Integer tableId);

    Order createNewOrderForTable(DiningTable table);

    Order appendItemsToExistingOrder(Order order, OrderCartDTO cartDTO);

    List<OrderItem> getItemsByOrder(Order order);

    List<OrderItem> getOrderItemsByTableAndStatuses(Integer tableId, List<OrderItem.ItemStatus> statuses);

    void updateItemStatus(Integer id, OrderItem.ItemStatus itemStatus);

    boolean existsOrderItemByOrderIdAndTableId(Integer orderId, Integer tableId);

    Optional<Order> findLastedOrderByTableId(Integer tableId);

    void remove(Integer id);
}

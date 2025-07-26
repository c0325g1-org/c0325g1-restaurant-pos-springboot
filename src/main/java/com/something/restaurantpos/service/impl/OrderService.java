package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.OrderCartDTO;
import com.something.restaurantpos.dto.OrderItemDTO;
import com.something.restaurantpos.entity.*;
import com.something.restaurantpos.repository.*;
import com.something.restaurantpos.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final IMenuItemRepository menuItemRepository;
    private final IDiningTableRepository diningTableRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public Order placeOrder(OrderCartDTO cartDTO, Integer employeeId) {
        DiningTable table = diningTableRepository.findById(cartDTO.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Order order = new Order();
        order.setTable(table);
        order.setEmployee(employee);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.OPEN);

        order = orderRepository.save(order);

        for (OrderItemDTO dto : cartDTO.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setStatus(OrderItem.ItemStatus.NEW);

            orderItemRepository.save(orderItem);
        }

        table.setStatus(DiningTable.TableStatus.SERVING);
        diningTableRepository.save(table);

        return order;
    }

    @Override
    public boolean existsOpenOrderByTableId(Integer id) {
        return diningTableRepository.existsOpenOrderById(id);
    }

    @Override
    public Optional<Order> findOpenOrderByTableId(Integer tableId) {
        List<Order> orders = orderRepository.findByTableIdAndStatus(tableId, Order.OrderStatus.OPEN);
        return orders.stream().findFirst();
    }

    @Override
    public Order createNewOrderForTable(DiningTable table) {
        Order order = new Order();
        order.setTable(table);
        order.setStatus(Order.OrderStatus.OPEN);
        order.setOrderTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Order appendItemsToExistingOrder(Order order, OrderCartDTO cartDTO) {
        for (OrderItemDTO dto : cartDTO.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setStatus(OrderItem.ItemStatus.NEW);

            orderItemRepository.save(orderItem);
        }
        return order;
    }

    @Override
    public List<OrderItem> getItemsByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    @Override
    public List<OrderItem> getOrderItemsByTableAndStatuses(Integer tableId, List<OrderItem.ItemStatus> statuses) {
        return orderItemRepository.findByOrder_Table_IdAndStatusIn(tableId, statuses);
    }

    @Override
    public void updateItemStatus(Integer id, OrderItem.ItemStatus itemStatus) {
        OrderItem item = orderItemRepository.findById(id).orElseThrow();
        item.setStatus(itemStatus);
        orderItemRepository.save(item);
    }
}

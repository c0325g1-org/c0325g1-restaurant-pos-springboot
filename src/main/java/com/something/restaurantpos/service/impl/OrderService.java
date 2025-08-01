package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.repository.IOrderRepository;
import com.something.restaurantpos.dto.NotificationDTO;
import com.something.restaurantpos.dto.OrderCartDTO;
import com.something.restaurantpos.dto.OrderItemDTO;
import com.something.restaurantpos.entity.*;
import com.something.restaurantpos.repository.*;
import com.something.restaurantpos.service.INotificationService;
import com.something.restaurantpos.service.IOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
    private final INotificationService notificationService;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order placeOrder(OrderCartDTO cartDTO, Integer employeeId) {
        DiningTable table = diningTableRepository.findById(cartDTO.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Order order = createOrder(table, employee);
        List<OrderItem> orderItems = createOrderItems(order, cartDTO.getItems());
        sendNewOrderNotification(order, orderItems);
        return order;
    }

    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order appendItemsToExistingOrder(Order order, OrderCartDTO cartDTO) {
        List<OrderItem> orderItems = createOrderItems(order, cartDTO.getItems());
        sendNewOrderNotification(order, orderItems);
        return order;
    }

    private Order createOrder(DiningTable table, Employee employee) {
        Order order = new Order();
        order.setTable(table);
        order.setEmployee(employee);
        order.setStatus(Order.OrderStatus.OPEN);
        return orderRepository.save(order);
    }

    private List<OrderItem> createOrderItems(Order order, List<OrderItemDTO> itemDTOs) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO dto : itemDTOs) {
            MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setStatus(OrderItem.ItemStatus.NEW);
            orderItems.add(orderItemRepository.save(orderItem));
        }
        return orderItems;
    }

    private void sendNewOrderNotification(Order order, List<OrderItem> orderItems) {
        NotificationDTO notification = new NotificationDTO();
        String message = order.getTable().getName() + " có gọi " + orderItems.size() + " món mới!";
        notification.setMessage(message);
        notification.setOrder(order);
        notification.setOrderItems(orderItems);
        notification.setNewOrder(true);
        notificationService.create(message, Notification.NotificationType.INFO, Role.UserRole.ROLE_KITCHEN);
        notificationService.sendToUser(notification, Role.UserRole.ROLE_KITCHEN);
    }

    @Override
    public Order findByIdOrThrow(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Optional<Order> findLastedOpenOrderByTableId(Integer tableId) {
        return orderRepository.findByTableIdAndStatusOrderByCreatedAtDesc(tableId, Order.OrderStatus.OPEN)
                .stream().findFirst();
    }

    @Override
    public void save(Order order) {
    }

    @Override
    public Order createNewOrderForTable(DiningTable table) {
        Order order = new Order();
        order.setTable(table);
        order.setStatus(Order.OrderStatus.OPEN);
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Integer id) {
    }

    @Override
    public List<OrderItem> getItemsByOrder(Order order) {
        return orderItemRepository.findAllByOrder(order);
    }

    @Override
    public Page<Order> findTrash(Pageable pageable) {
        return null;
    }

    @Override
    public List<OrderItem> getOrderItemsByTableAndStatuses(Integer tableId, List<OrderItem.ItemStatus> statuses) {
        return orderItemRepository.findAllByOrder_Table_IdAndStatusIn(tableId, statuses);
    }

    @Override
    public void softDelete(Integer id) {
    }

    @Override
    public void updateItemStatus(Integer id, OrderItem.ItemStatus itemStatus) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        item.setStatus(itemStatus);
        orderItemRepository.save(item);
    }

    @Override
    public void restore(Integer id) {
    }

    @Override
    public boolean existsOrderItemByOrderIdAndTableId(Integer orderId, Integer tableId) {
        return orderItemRepository.existsOrderItemByOrderIdAndOrder_Table_Id(orderId, tableId);
    }

    @Override
    public long countDeleted() {
        return 0;
    }

    @Override
    public Optional<Order> findLastedOrderByTableId(Integer tableId) {
        return orderRepository.findByTableIdOrderByCreatedAtDesc(tableId)
                .stream().findFirst();
    }

    @Override
    public void remove(Integer id) {
        orderRepository.deleteById(id);
    }
}

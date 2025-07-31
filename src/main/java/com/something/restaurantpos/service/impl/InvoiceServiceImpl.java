package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.dto.OrderItemDTO;
import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.*;
import com.something.restaurantpos.repository.*;
import com.something.restaurantpos.service.IInvoiceService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements IInvoiceService {
    @Autowired
    private IOrderItemRepository orderItemRepository;
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IMenuItemRepository menuItemRepository;
    @Autowired
    private IInvoiceRepository invoiceRepository;
    @Autowired
    private IIPaymentRepository paymentRepository;
    @Autowired
    private IDiningTableRepository tableRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<InvoiceDto> findAllDto() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInvoiceWithItems(Integer id, InvoiceDto dto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setNote(dto.getNote());
        invoice.setPaid(Boolean.TRUE.equals(dto.getPaid()));

        Order order = invoice.getOrder();
        if (order == null) {
            throw new RuntimeException("Invoice has no associated order");
        }

        List<OrderItem> existingItems = orderItemRepository.findAllByOrder_Id(order.getId());
        List<OrderItem> updatedItems = new ArrayList<>();
        List<Integer> updatedItemIds = new ArrayList<>();

        for (OrderItemDTO itemDto : dto.getOrderItems()) {
            OrderItem item;
            if (itemDto.getMenuItemId() != null) {
                item = existingItems.stream()
                        .filter(i -> i.getId().equals(itemDto.getMenuItemId()))
                        .findFirst()
                        .orElse(null);
                if (item != null) {
                    item.setQuantity(itemDto.getQuantity());
                    item.setPrice(itemDto.getPrice());
                    item.setStatus(OrderItem.ItemStatus.NEW);
                    updatedItems.add(item);
                    updatedItemIds.add(item.getId());
                }
            } else {
                item = new OrderItem();
                item.setOrder(order);
                item.setMenuItem(menuItemRepository.findByName(itemDto.getMenuItemName())
                        .orElseThrow(() -> new RuntimeException("Item not found: " + itemDto.getMenuItemName())));
                item.setPrice(itemDto.getPrice());
                item.setQuantity(itemDto.getQuantity());
                item.setStatus(OrderItem.ItemStatus.NEW);
                updatedItems.add(item);
            }
        }

        List<OrderItem> itemsToDelete = existingItems.stream()
                .filter(item -> !updatedItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        orderItemRepository.deleteAll(itemsToDelete);

        // Lưu đơn hàng trước
        orderRepository.save(order); // 🔥 THÊM DÒNG NÀY

        BigDecimal total = updatedItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(total);

        invoiceRepository.save(invoice); // lưu invoice cuối cùng
    }

    @Override
    public Page<InvoiceDto> findAllDtoPage(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public void processPayment(PaymentDto dto) {
        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (invoice.isPaid()) {
            throw new IllegalStateException("Hóa đơn đã được thanh toán!");
        }

        // Cập nhật hóa đơn
        invoice.setTotalAmount(dto.getAmount());
        invoice.setPaid(true);
        invoiceRepository.save(invoice);
        Order order = invoice.getOrder();
        order.setStatus(Order.OrderStatus.CLOSED);
        orderRepository.save(order);

        DiningTable table = order.getTable();
        table.setStatus(DiningTable.TableStatus.EMPTY);
        tableRepository.save(table);

        // Ghi log thanh toán nếu có bảng `payment`
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(dto.getAmount());
        payment.setMethod(Payment.Method.valueOf(dto.getMethod()));
        payment.setPaymentTime(dto.getPaymentTime());
        paymentRepository.save(payment);
    }


    @Override
    public List<Invoice> findReadyToCheckout() {
        return List.of();
    }

    @Override
    public InvoiceDto findDtoById(Integer id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với id: " + id));
        return convertToDto(invoice);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice findById(Integer id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    @Override
    public Invoice findByIdOrThrow(Integer id) {
        return null;
    }

    @Override
    public void save(Invoice invoice) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Page<Invoice> findTrash(Pageable pageable) {
        return null;
    }

    @Override
    public void softDelete(Integer id) {

    }

    @Override
    public void restore(Integer id) {

    }

    @Override
    public long countDeleted() {
        return 0;
    }

    private InvoiceDto convertToDto(Invoice invoice) {
        InvoiceDto dto = modelMapper.map(invoice, InvoiceDto.class);
        dto.setOrderId(invoice.getOrder().getId());
        dto.setTableName(invoice.getOrder().getTable().getName());
        dto.setEmployeeName(invoice.getOrder().getEmployee().getName());
        dto.setOrderTime(invoice.getOrder().getCreatedAt());

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(invoice.getOrder().getId());
        List<OrderItemDTO> itemDtos = orderItems.stream()
                .filter(orderItem -> "SERVED".equals(orderItem.getStatus().name())) // 🔥 Chỉ lấy món SERVED
                .map(orderItem -> {
                    OrderItemDTO itemDto = new OrderItemDTO();
                    itemDto.setMenuItemName(orderItem.getMenuItem().getName());
                    itemDto.setQuantity(orderItem.getQuantity());
                    itemDto.setPrice(orderItem.getPrice());
                    return itemDto;
                }).collect(Collectors.toList());

        dto.setOrderItems(itemDtos);
        BigDecimal totalAmount = itemDtos.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalAmount(totalAmount);

        return dto;
    }

    @Override
    public Invoice findById(String id) {
        return null;
    }

    @Override
    public Invoice findByIdOrThrow(String id) {
        return invoiceRepository.findById(Integer.valueOf(id)).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public Invoice findByOrderId(Integer orderId) {
        return invoiceRepository.findByOrder_Id(orderId);
    }

    @Override
    public Invoice createInvoiceFromOrder(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setDeleted(false);
        return invoiceRepository.save(invoice);
    }
}
    

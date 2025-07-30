package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.dto.OrderItemDto;
import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.entity.Payment;
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

        List<OrderItem> existingItems = order.getOrderItems();
        List<OrderItem> updatedItems = new ArrayList<>();
        List<Integer> updatedItemIds = new ArrayList<>();

        for (OrderItemDto itemDto : dto.getOrderItems()) {
            OrderItem item;
            if (itemDto.getId() != null) {
                item = existingItems.stream()
                        .filter(i -> i.getId().equals(itemDto.getId()))
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
                item.setMenuItem(menuItemRepository.findByName(itemDto.getItemName())
                        .orElseThrow(() -> new RuntimeException("Item not found: " + itemDto.getItemName())));
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
        order.setOrderItems(updatedItems);

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
        invoice.setPaid(true);
        invoiceRepository.save(invoice);

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
        dto.setOrderTime(invoice.getOrder().getOrderTime());

        List<OrderItemDto> itemDtos = invoice.getOrder().getOrderItems().stream()
                .filter(orderItem -> "SERVED".equals(orderItem.getStatus().name())) // 🔥 Chỉ lấy món SERVED
                .map(orderItem -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setItemName(orderItem.getMenuItem().getName());
                    itemDto.setQuantity(orderItem.getQuantity());
                    itemDto.setPrice(orderItem.getPrice());
                    itemDto.setTotal(orderItem.getPrice().multiply(
                            java.math.BigDecimal.valueOf(orderItem.getQuantity())
                    ));
                    return itemDto;
                }).collect(Collectors.toList());

        dto.setOrderItems(itemDtos);

        // Tổng tiền = tổng các món đã SERVED
        dto.setTotalAmount(itemDtos.stream()
                .map(OrderItemDto::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        return dto;
    }
}

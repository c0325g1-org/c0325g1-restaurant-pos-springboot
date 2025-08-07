package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime dateTime;
    private Integer people;
    private String note;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private DiningTable table;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.NEW;

    public enum BookingStatus {
        NEW, CONFIRMED, CANCELLED, SERVED, NOSHOW;

        public String getLabel() {
            return switch (this) {
                case NEW -> "Mới đặt";
                case CONFIRMED -> "Đã xác nhận";
                case CANCELLED -> "Đã huỷ";
                case SERVED -> "Đã phục vụ";
                case NOSHOW -> "Không đến";
            };
        }

        public String getBadgeClass() {
            return switch (this) {
                case NEW -> "bg-secondary";
                case CONFIRMED -> "bg-primary";
                case CANCELLED -> "bg-danger";
                case SERVED -> "bg-success";
                case NOSHOW -> "bg-warning text-dark";
            };
        }
    }

    private Boolean reminded = false;
}

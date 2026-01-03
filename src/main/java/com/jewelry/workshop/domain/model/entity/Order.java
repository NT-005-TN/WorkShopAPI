package com.jewelry.workshop.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private Long orderNumber;

    @Column(name = "status", length = 20)
    private String status = STATUS_PENDING;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", precision = 12, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "order_datetime")
    private Instant orderDatetime;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Константы для статусов заказа
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_DELIVERED = "DELIVERED";

    public Order() {
        this.orderDatetime = Instant.now();
    }

    public void calculateTotals(){
        if (client == null)
            throw new IllegalStateException("Заказ должен иметь клиента");

        if (totalAmount.compareTo(BigDecimal.ZERO) > 0){
            return;
        }

        if(client.isPermanentClient())
            discountAmount = totalAmount.multiply(new BigDecimal("0.10"));
        else
            discountAmount = BigDecimal.ZERO;

        finalAmount = totalAmount.subtract(discountAmount);

        if(finalAmount.compareTo(BigDecimal.ZERO) < 0)
            totalAmount = BigDecimal.ZERO;
    }

    public void updateStatus(String newStatus){
        if(!isValidStatus(newStatus))
            throw new IllegalArgumentException(
                    "Недопустимый статус заказа: " + newStatus
            );

        this.status = newStatus;

        if(STATUS_COMPLETED.equals(newStatus) || STATUS_DELIVERED.equals(newStatus))
            this.orderDatetime = Instant.now();
    }

    public boolean canBeCancelled() {
        return STATUS_PENDING.equals(status) || STATUS_PROCESSING.equals(status);
    }

    public void cancel(){
        if(!canBeCancelled())
            throw new IllegalStateException(
                    String.format(
                            "Невозможно отменить заказ со статусом: %s", status
                    )
            );
        updateStatus(STATUS_CANCELLED);
    }

    private boolean isValidStatus(String status){
        return status != null && (
                STATUS_PENDING.equals(status) ||
                        STATUS_PROCESSING.equals(status) ||
                        STATUS_COMPLETED.equals(status) ||
                        STATUS_CANCELLED.equals(status) ||
                        STATUS_DELIVERED.equals(status)
        );
    }

    public String getOrderInfo(){
        return String.format(
                "Заказ #%d от %s, Статус: %s, Сумма: %,.2f руб.",
                id,
                client != null ? client.getFullName() : "Неизвестный клиент",
                status, finalAmount.doubleValue()
        );
    }
}

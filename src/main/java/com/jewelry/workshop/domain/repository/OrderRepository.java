package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    List<Order> findByOrderDatetime(LocalDateTime orderDatetime);
    List<Order> findByDiscountAmount(BigDecimal discount);

    List<Order> findByClientId(Long clientId);
    List<Order> findByClientIdAndStatus(Long clientId, String status);

    List<Order> findByOrderDatetimeBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Page<Order> findAll(Pageable pageable);
    Page<Order> findByClientId(Long ClientId, Pageable pageable);
    Page<Order> findBYStatus(String status, Pageable pageable);

    @Query("""
            SELECT DATE(o.order_datetime) as orderDate,
            SUM(o.final_amount) as dailyRevenue
            FROM orders o
            WHERE o.order_datetime >= :startDate
            GROUP BY DATE(o.order_datetime)
            ORDER BY orderDate DESC
            """)
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate);

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.orderItems oi
            WHERE oi.product.id = :productId
            """)
    List<Order> findOrdersContainingProduct(@Param("productId") Long productId);

    @Query("""
            SELECT o FROM Order o ORDER BY o.finalAmount DESC
            """)
    List<Order> findTopExpensiveOrders(Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            WHERE o.orderDatetime >= :oneMonthAgo
            """)
    List<Order> findRecentOrders(@Param("oneMonthAgo")LocalDateTime oneMonthAgo);
}

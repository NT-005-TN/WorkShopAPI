package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.Instant;
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
    Page<Order> findByStatus(String status, Pageable pageable);
    Page<Order> findByClientIdAndStatus(Long clientId, String status, Pageable pageable);

    List<Order> findByCompletedAtIsNotNull();
    List<Order> findByCompletedAtBetween(Instant start, Instant end);
    List<Order> findByCompletedAtAfter(Instant date);

    Long countByClientId(Long clientId);

    @Query("""
        SELECT DATE(CAST(o.orderDatetime AS date)) as orderDate, 
               COUNT(o) as orderCount,
               SUM(o.finalAmount) as dailyRevenue,
               AVG(o.finalAmount) as avgOrderValue
        FROM Order o 
        WHERE o.orderDatetime >= :startDate
        GROUP BY DATE(CAST(o.orderDatetime AS date))
        ORDER BY orderDate DESC
        """)
    List<Object[]> getDailyRevenue(@Param("startDate") Instant startDate);

    @Query("""
        SELECT o.client.id, c.lastName, c.firstName, 
               COUNT(o) as orderCount,
               SUM(o.finalAmount) as totalSpent,
               MAX(o.orderDatetime) as lastOrderDate
        FROM Order o 
        JOIN o.client c 
        WHERE o.orderDatetime BETWEEN :startDate AND :endDate
        GROUP BY o.client.id, c.lastName, c.firstName 
        ORDER BY totalSpent DESC
        """)
    List<Object[]> getClientStatisticsInPeriod(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // Исправленные сложные запросы
    @Query("""
        SELECT DISTINCT o FROM Order o 
        JOIN o.orderItems oi 
        WHERE oi.product.id = :productId
        AND o.orderDatetime >= :startDate
        """)
    List<Order> findOrdersContainingProductSince(
            @Param("productId") Long productId,
            @Param("startDate") Instant startDate
    );

    @Query("SELECT o FROM Order o ORDER BY o.finalAmount DESC")
    List<Order> findTopExpensiveOrders(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDatetime >= :date")
    List<Order> findRecentOrders(@Param("date") Instant date);

    @Query("""
        SELECT o.status, COUNT(o) as count 
        FROM Order o 
        WHERE o.orderDatetime BETWEEN :startDate AND :endDate
        GROUP BY o.status
        """)
    List<Object[]> getOrderStatusDistribution(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT COUNT(o) FROM Order o WHERE o.client.id = :clientId")
    Long countOrdersByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.client.id = :clientId")
    BigDecimal getTotalSpentByClientId(@Param("clientId") Long clientId);

    @Query("SELECT MAX(o.orderDatetime) FROM Order o WHERE o.client.id = :clientId")
    LocalDateTime getLastOrderDateByClientId(@Param("clientId") Long clientId);

    @Query("""
        SELECT EXTRACT(MONTH FROM o.orderDatetime) as month,
               EXTRACT(YEAR FROM o.orderDatetime) as year,
               COUNT(o) as orderCount,
               SUM(o.finalAmount) as monthlyRevenue
        FROM Order o 
        WHERE o.orderDatetime BETWEEN :startDate AND :endDate
        GROUP BY EXTRACT(YEAR FROM o.orderDatetime), EXTRACT(MONTH FROM o.orderDatetime)
        ORDER BY year DESC, month DESC
        """)
    List<Object[]> getMonthlySalesReport(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );


    @Query("""
        SELECT o FROM Order o 
        WHERE (:clientId IS NULL OR o.client.id = :clientId)
        AND (:status IS NULL OR o.status = :status)
        AND (:startDate IS NULL OR o.orderDatetime >= :startDate)
        AND (:endDate IS NULL OR o.orderDatetime <= :endDate)
        AND (:minAmount IS NULL OR o.finalAmount >= :minAmount)
        AND (:maxAmount IS NULL OR o.finalAmount <= :maxAmount)
        ORDER BY o.orderDatetime DESC
        """)
    Page<Order> findOrdersByCriteria(
            @Param("clientId") Long clientId,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

    @Query("""
        SELECT AVG(o.finalAmount) 
        FROM Order o 
        WHERE o.client.isPermanent = true 
        AND o.orderDatetime BETWEEN :startDate AND :endDate
        """)
    BigDecimal getAverageOrderValueForPermanentClients(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
        SELECT o FROM Order o 
        WHERE o.updatedAt >= :sinceDate
        ORDER BY o.updatedAt DESC
        """)
    List<Order> findRecentlyUpdatedOrders(@Param("sinceDate") Instant sinceDate);

}
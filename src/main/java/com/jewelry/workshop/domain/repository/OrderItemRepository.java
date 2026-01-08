package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    //Поисковые
    List<OrderItem> findByQuantityGreaterThan(Integer quantity);
    List<OrderItem> findByTotalPriceGreaterThan(BigDecimal orderPrice);

    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);
    List<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);

    List<OrderItem> findByOrderIdIn(List<Long> OrderIds);
    List<OrderItem> findByProductIdIn(List<Long> productIds);

    List<OrderItem> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<OrderItem> findByTotalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<OrderItem> findByCreatedAtBetween(Instant startDate, Instant endDate);
    List<OrderItem> findByCreatedAtAfter(Instant date);

    //С Query
    @Query("""
        SELECT SUM(oi.quantity)
        FROM OrderItem oi
        WHERE oi.product.id = :productId
        AND oi.createdAt BETWEEN :startDate AND :endDate
        """)
    Long getQuantitySoldForProductInPeriod(
            @Param("productId") Long productId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
            SELECT SUM(oi.totalPrice)
            FROM OrderItem oi
            WHERE oi.product.id = :productId
            AND oi.createdAt BETWEEN :startDate AND :endDate
            """)
    BigDecimal getRevenueForProductInPeriod(
            @Param("productId") Long productId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
            SELECT oi.product.id, SUM(oi.quantity) as totalQuantity, SUM(oi.totalPrice) as totalRevenue
            FROM OrderItem oi
            WHERE oi.createdAt BETWEEN :startDate and :endDate
            GROUP BY oi.product.id
            ORDER BY totalQuantity DESC
            """)
    List<Object[]> findBestSellingProductsInPeriod(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.product
        WHERE oi.order.id = :orderId
        ORDER BY oi.createdAt
        """)
    List<OrderItem> findOrderItemsWithProductDetails(@Param("orderId") Long orderId);

    @Query("""
        SELECT oi.product.type, 
               SUM(oi.quantity) as totalSold,
               SUM(oi.totalPrice) as totalRevenue,
               AVG(oi.unitPrice) as avgPrice
        FROM OrderItem oi 
        WHERE oi.createdAt BETWEEN :startDate AND :endDate
        GROUP BY oi.product.type 
        ORDER BY totalRevenue DESC
        """)
    List<Object[]> getSalesStatisticsByProductType(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
        SELECT oi1.product.id as product1, oi2.product.id as product2, COUNT(*) as frequency
        FROM OrderItem oi1 
        JOIN OrderItem oi2 ON oi1.order.id = oi2.order.id 
        WHERE oi1.product.id < oi2.product.id
        AND oi1.createdAt BETWEEN :startDate AND :endDate
        GROUP BY oi1.product.id, oi2.product.id 
        ORDER BY frequency DESC
        """)
    List<Object[]> findFrequentProductCombinations(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    @Query("""
        SELECT AVG(itemCount) 
        FROM (
            SELECT COUNT(oi) as itemCount 
            FROM OrderItem oi 
            WHERE oi.createdAt BETWEEN :startDate AND :endDate
            GROUP BY oi.order.id
        )
        """)
    Double getAverageItemsPerOrder(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("""
        SELECT DISTINCT oi.order.id 
        FROM OrderItem oi 
        WHERE oi.product.id = :productId
        AND oi.createdAt BETWEEN :startDate AND :endDate
        """)
    List<Long> findOrderIdsContainingProductInPeriod(
            @Param("productId") Long productId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}

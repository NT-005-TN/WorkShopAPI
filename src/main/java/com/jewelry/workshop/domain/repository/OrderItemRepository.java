package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByQuantityGreaterThan(Integer quantity);
    List<OrderItem> findByTotalPriceGreaterThan(BigDecimal orderPrice);

    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductID(Long productId);
    List<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);

    @Query("""
            SELECT SUM(oi.quantity) FROM OrderItem oi
            WHERE oi.product.id = :productId
            """)
    Long getTotalQuantitySoldForProduct(@Param("productId") Long productId);

    @Query("""
            SELECT oi.product.id, SUM(oi.quantity) as totalQuantity
            FROM OrderItem oi
            GROUP BY oi.product.id
            ORDER BY totalQUANTITY DESC
            """)
    List<Object[]> findBestSellingProducts(Pageable pageable);

    @Query("""
            SELECT oi FROM OrderItem oi
            JOIN FETCH oi.product
            WHERE oi.order.id = :orderId
            """)
    List<OrderItem> findOrderItemWithProductDetails(@Param("orderId") Long orderId);
}

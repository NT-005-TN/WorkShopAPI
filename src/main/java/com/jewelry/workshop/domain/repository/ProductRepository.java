package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByType(String type);
    List<Product> findByWeightGreaterThan(BigDecimal weight);
    List<Product> findByInStockGreaterThan(Integer amount);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByTypeAndInStockGreaterThan(String type, Integer minStock);
    List<Product> findByPriceGreaterThanAndWeightLessThan(BigDecimal minPrice, BigDecimal maxWeight);

    List<Product> findByTypeOrderByPriceDesc(String type);
    List<Product> findByInStockGreaterThanOrderByNameAsc(Integer minStock);

    Page<Product> findAll(Pageable pageable);
    Page<Product> findByType(String type, Pageable pageable);

    @Query("""
            SELECT DISTINCT p FROM Product p
            JOIN p.materials m
            WHERE m.id = :materialId
            """)
    List<Product> findByMaterialId(@Param("materialId") Long materialId);

    @Query("""
            SELECT p FROM Product p
            WHERE p.inStock <= :threshold
            """)
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    @Query("""
            SELECT p, COUNT(oi) as orderCount
            FROM Product p
            LEFT JOIN OrderItem oi ON p.id = oi.product.id
            GROUP BY p.id
            ORDER BY orderCount DESC
            """)
    List<Product> findAvailableProductsNeverOrdered();

    @Query("""
            SELECT p.type, COUNT(p), AVG(p.price), SUM(p.inStock)
            FROM Product p
            GROUP BY p.type
            """)
    List<Object[]> getProductStatisticsByType();

    @Query("""
            SELECT p FROM Product p
            WHERE p.price > (
            SELECT AVG(p2.price) FROM Product p2 WHERE p2.type = p.type
            )""")
    List<Product> findProductsAboveAveragePriceForType();
}

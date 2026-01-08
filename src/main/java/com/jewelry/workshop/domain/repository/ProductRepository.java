package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Основные методы поиска
    List<Product> findByType(String type);
    List<Product> findByWeightGreaterThan(BigDecimal weight);
    List<Product> findByInStockGreaterThan(Integer amount);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByNameContainingIgnoreCase(String name);

    // Комбинированные поиски
    List<Product> findByTypeAndInStockGreaterThan(String type, Integer minStock);
    List<Product> findByPriceGreaterThanAndWeightLessThan(BigDecimal minPrice, BigDecimal maxWeight);

    // Сортировка
    List<Product> findByTypeOrderByPriceDesc(String type);
    List<Product> findByInStockGreaterThanOrderByNameAsc(Integer minStock);

    // Пагинация
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByType(String type, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Поиск по артикулу
    Optional<Product> findBySku(String sku);

    // Поиск по доступности
    List<Product> findByIsAvailableTrue();
    List<Product> findByIsAvailableTrueAndInStockGreaterThan(Integer minStock);

    // Исправленные сложные запросы
    @Query("""
        SELECT DISTINCT p FROM Product p 
        JOIN p.productMaterials pm 
        WHERE pm.material.id = :materialId
        """)
    List<Product> findByMaterialId(@Param("materialId") Long materialId);

    @Query("SELECT p FROM Product p WHERE p.inStock <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    @Query("""
        SELECT p FROM Product p 
        WHERE p.isAvailable = true 
        AND p.id NOT IN (
            SELECT oi.product.id FROM OrderItem oi
        )
        """)
    List<Product> findAvailableProductsNeverOrdered();

    @Query("""
        SELECT p.type, 
               COUNT(p) as count, 
               AVG(p.price) as avgPrice, 
               SUM(p.inStock) as totalStock,
               MIN(p.price) as minPrice,
               MAX(p.price) as maxPrice
        FROM Product p 
        GROUP BY p.type
        """)
    List<Object[]> getProductStatisticsByType();

    @Query("""
        SELECT p FROM Product p 
        WHERE p.price > (
            SELECT AVG(p2.price) FROM Product p2 WHERE p2.type = p.type
        )
        AND p.isAvailable = true
        """)
    List<Product> findProductsAboveAveragePriceForType();

    // Новые методы по ТЗ
    @Query("""
        SELECT p FROM Product p 
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT p, 
               COUNT(oi) as orderCount,
               SUM(oi.quantity) as totalSold,
               SUM(oi.totalPrice) as totalRevenue
        FROM Product p 
        LEFT JOIN OrderItem oi ON p.id = oi.product.id 
        AND oi.createdAt BETWEEN :startDate AND :endDate
        GROUP BY p 
        ORDER BY totalRevenue DESC NULLS LAST
        """)
    List<Object[]> getProductSalesStatistics(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Product p 
        WHERE p.isAvailable = true 
        AND p.inStock > 0
        AND (:type IS NULL OR p.type = :type)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:minWeight IS NULL OR p.weight >= :minWeight)
        AND (:maxWeight IS NULL OR p.weight <= :maxWeight)
        ORDER BY 
            CASE WHEN :sortBy = 'price_asc' THEN p.price END ASC,
            CASE WHEN :sortBy = 'price_desc' THEN p.price END DESC,
            CASE WHEN :sortBy = 'name' THEN p.name END ASC,
            CASE WHEN :sortBy = 'weight' THEN p.weight END ASC
        """)
    Page<Product> findAvailableProductsByCriteria(
            @Param("type") String type,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minWeight") BigDecimal minWeight,
            @Param("maxWeight") BigDecimal maxWeight,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p " +
            "WHERE p.updatedAt >= :sinceDate " +
            "AND (" +
            "    (p.inStock < :lowStockThreshold) " +
            "    OR (p.isAvailable = true AND p.inStock <= 0) " +
            "    OR (p.isAvailable = false AND p.inStock > 0)" +
            ")")
    List<Product> findProductsNeedingAttention(@Param("sinceDate") Instant sinceDate,
                                               @Param("lowStockThreshold") Integer lowStockThreshold);

    // Прогнозирование продаж
    @Query("""
        SELECT p, 
               AVG(monthlySales.sales) as avgMonthlySales,
               p.inStock / NULLIF(AVG(monthlySales.sales), 0) as monthsOfSupply
        FROM Product p 
        LEFT JOIN (
            SELECT oi.product.id as productId, 
                   EXTRACT(MONTH FROM oi.createdAt) as month,
                   EXTRACT(YEAR FROM oi.createdAt) as year,
                   SUM(oi.quantity) as sales
            FROM OrderItem oi 
            WHERE oi.createdAt >= :startDate
            GROUP BY oi.product.id, EXTRACT(YEAR FROM oi.createdAt), EXTRACT(MONTH FROM oi.createdAt)
        ) monthlySales ON p.id = monthlySales.productId
        WHERE p.isAvailable = true
        GROUP BY p
        HAVING p.inStock / NULLIF(AVG(monthlySales.sales), 0) < :minMonthsSupply
        """)
    List<Object[]> findProductsNeedingRestock(
            @Param("startDate") Instant startDate,
            @Param("minMonthsSupply") Double minMonthsSupply
    );

    // Поиск рекомендуемых товаров
    @Query("""
        SELECT p2 
        FROM OrderItem oi1 
        JOIN OrderItem oi2 ON oi1.order.id = oi2.order.id 
        JOIN Product p2 ON oi2.product.id = p2.id 
        WHERE oi1.product.id = :productId 
        AND oi2.product.id != :productId
        AND p2.isAvailable = true
        AND p2.inStock > 0
        GROUP BY p2 
        ORDER BY COUNT(*) DESC
        """)
    List<Product> findFrequentlyBoughtTogether(
            @Param("productId") Long productId,
            Pageable pageable
    );
}
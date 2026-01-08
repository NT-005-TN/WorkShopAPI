package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Material;
import com.jewelry.workshop.domain.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    // Поисковые методы
    Optional<Material> findByName(String name);
    Optional<Material> findByNameIgnoreCase(String name);  // Исправлено: findyName → findByName
    List<Material> findByNameContainingIgnoreCase(String name);
    List<Material> findByNameIn(List<String> names);  // Исправлено: name → names (множественное число)

    List<Material> findByCreatedAtAfter(Instant startDate);
    List<Material> findByCreatedAtBetween(Instant start, Instant end);
    List<Material> findByUpdatedAtAfter(Instant date);

    Page<Material> findAll(Pageable pageable);
    Page<Material> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Material> findByCreatedAtBetween(Instant start, Instant end, Pageable pageable);
    List<Material> findByCreatedAtBefore(Instant date);

    boolean existsByName(String name);

    List<Material> findAllByOrderByNameAsc();
    List<Material> findAllByOrderByCreatedAtDesc();

    List<Material> findByDescriptionIsNotNull();
    List<Material> findByDescriptionContainingIgnoreCase(String keyword);

    // С Query
    @Query("SELECT COUNT(m) FROM Material m WHERE m.updatedAt > :date")
    Long countUpdatedAfter(@Param("date") Instant date);

    @Query("""
    SELECT EXTRACT(YEAR FROM m.createdAt) as year, 
           EXTRACT(MONTH FROM m.createdAt) as month, 
           COUNT(m) as count
    FROM Material m
    WHERE m.createdAt BETWEEN :startDate AND :endDate
    GROUP BY EXTRACT(YEAR FROM m.createdAt), EXTRACT(MONTH FROM m.createdAt)
    ORDER BY year DESC, month DESC
    """)
    List<Object[]> getMonthlyCreationStats(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT COUNT(m) FROM Material m")
    Long countAllMaterials();

    @Query("""
            SELECT m FROM Material m
            JOIN m.productMaterials pm
            WHERE pm.product.id = :productId                      
            """)
    List<Material> findByProductId(@Param("productId") Long productId);

    @Query("""
        SELECT pm.product FROM ProductMaterial pm
        JOIN pm.material m
        WHERE m.name = :materialName
        """)
    List<Product> findProductsByMaterialName(@Param("materialName") String materialName);

    @Query("SELECT m FROM Material m WHERE SIZE(m.productMaterials) = 0")
    List<Material> findUnusedMaterials();

    @Query("""
        SELECT m.name, COUNT(pm.product) as usageCount
        FROM Material m
        LEFT JOIN m.productMaterials pm
        GROUP BY m.name
        ORDER BY usageCount DESC
        """)
    List<Object[]> findMaterialUsageStatistics();

    @Query("""
           SELECT m FROM Material m
           WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Material> searchMaterials(@Param("keyword") String keyword);

    @Query("""
        SELECT m FROM Material m 
        WHERE m.createdAt >= :startDate 
        AND (m.name LIKE CONCAT('%', :searchTerm, '%') 
             OR m.description LIKE CONCAT('%', :searchTerm, '%'))
        ORDER BY m.createdAt DESC
        """)
    Page<Material> findRecentMaterialsWithSearch(
            @Param("startDate") Instant startDate,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("""
            SELECT m,
            COUNT(pm.product) as productCount,
            SUM(pm.quantity) as totalQuantityUsed
            FROM Material m
            LEFT JOIN m.productMaterials pm
            GROUP BY m
            HAVING COUNT(pm.product) > 0
            ORDER BY totalQuantityUsed DESC
        """)
    List<Object[]> findMaterialsWithUsageStatistics();

    @Query("""
        SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END
        FROM ProductMaterial pm
        WHERE pm.material.id = :materialId
        """)
    boolean isMaterialInUse(@Param("materialId") Long materialId);
}
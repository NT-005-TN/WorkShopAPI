package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Material;
import com.jewelry.workshop.domain.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    // Поисковые методы - генерируются сами
    Optional<Material> findByName(String name);
    List<Material> findByNameContainingIgnoreCase(String name);
    List<Material> findByCreatedAtAfter(LocalDateTime startDate);
    List<Material> findByCreatedAtBetween(LocalDateTime start, LocalDate end);
    List<Material> findByNameIn(List<String> names);


    // Другие методы
    @Query("""
            SELECT m FROM Material m
            JOIN m.products p
            WHERE p.id = :productId
            """)
    List<Material> findByProductId(@Param("productId") Long productId);

    @Query("""
            SELECT p FROM Product m
            JOIN p.materials m
            WHERE m.name = :materialName
            """)
    List<Product> findProductByMaterialName(@Param("materialName") String materialName);

    @Query("SELECT COUNT(m) FROM Material m")
    Long countAllMaterials();

    @Query("SELECT m FROM Material m WHERE m.products IS EMPTY")
    List<Material> findUnusedMaterial();
}
/**
 * Автоматически генерируемые методы
 *
 * findByПоле → WHERE поле = ?
 *
 * findByПолеGreaterThan → WHERE поле > ?
 *
 * findByПолеContaining → WHERE поле LIKE %?%
 *
 * findByПолеAfter → WHERE поле > ? (для дат)
 */
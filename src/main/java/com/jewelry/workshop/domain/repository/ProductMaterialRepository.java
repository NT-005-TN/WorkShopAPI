package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Material;
import com.jewelry.workshop.domain.model.entity.Product;
import com.jewelry.workshop.domain.model.entity.ProductMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMaterialRepository extends JpaRepository<ProductMaterial, Long> {

    // Поисковые
    Optional<ProductMaterial> findByProductAndMaterial(Product product, Material material);
    List<ProductMaterial> findByProduct(Product product);
    List<ProductMaterial> findByMaterial(Material material);

    List<ProductMaterial> findByProductId(Long productId);
    List<ProductMaterial> findByMaterialId(Long materialId);

    boolean existsByProductAndMaterial(Product product, Material material);
    boolean existsByProductIdAndMaterialId(Long productId, Long materialId);

    void deleteByProductAndMaterial(Product product, Material material);
    void deleteByProduct(Product product);
    void deleteByMaterial(Material material);

    // С Query
    @Query("SELECT COUNT(pm) FROM ProductMaterial pm WHERE pm.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(pm) FROM ProductMaterial pm WHERE pm.material.id = :materialId")
    long countByMaterialId(@Param("materialId") Long materialId);

    @Query("""
            SELECT pm.material, SUM(pm.quantity) as totalQuantity
            FROM ProductMaterail pm
            WHERE pm.product.id = :productId
            GROUP BY pm.material
        """)
    List<Object[]> findMaterialUsageByProduct(@Param("productId") Long productId);

    @Query("""
            SELECT pm.product, SUM(pm.quantity) as materialUsage
            FROM ProductMaterial pm
            WHERE pm.material.id = :materialId
            GROUP BY pm.product
            ORDER BY materialUsage DESC
                """)
    List<Object[]> findProductByMaterialUsage(@Param("materialId") Long materialId);

    @Query("""
            SELECT m, SUM(pm.quantity) as totalUsed
            FROM ProductMaterial pm
            JOIN pm.material m
            GROUP BY m
            ORDER BY totalUsed DESC
            """)
    List<Object[]> findMostUsedMaterials();

    @Query("""
            SELECT DISTINCT pm.product
            FROM ProductMaterial pm
            WHERE pm.material.id IN :materialIds
           """)
    List<Product> findProductUsingMaterials(@Param("materialIds") List<Long> materialIds);

    @Query("""
            SELECT SUM(pm.quantity * m.price)
            FROM ProductMaterial pm
            JOIN pm.material m
            WHERE pm.product.id = :productId
            """)
    BigDecimal calculateMaterialCostForProduct(@Param("productId") Long productId);

    @Query("""
            SELECT pm.material
            FROM ProductMaterial pm
            WHERE pm.product.id = :productId
            AND pm.quantity > (
                    SELECT m.inStock FROM Material m WHERE pm.material.id = m.id
                        )
            """)
    List<Material> findInsufficientMaterialsForProduct(@Param("productId") Long productId);
}
package com.jewelry.workshop.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "sku", length = 50)
    private String sku;

    @Column(name = "weight", nullable = false, precision = 8, scale = 3)
    private BigDecimal weight;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "in_stock")
    private Integer inStock = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToMany(mappedBy = "product")
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductMaterial> productMaterials = new HashSet<>();


    public boolean isAvailable(){
        return inStock > 0;
    }

    public void decreaseStock(Integer quantity){
        if(quantity == null || quantity <= 0)
            throw new IllegalArgumentException("Количество должно быть положительным");

        if(inStock < quantity)
            throw new IllegalArgumentException(
                    "Недостаточно товара на складе. Доступно: " + inStock +
                            ", а запрошено: " + quantity
            );

        this.inStock -= quantity;
    }

    public void increaseStock(Integer quantity){
        if(quantity == null || quantity <= 0)
            throw new IllegalArgumentException(
                    "Количество должно быть положительным"
            );
        this.inStock += quantity;
    }
}

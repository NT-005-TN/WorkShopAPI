package com.jewelry.workshop.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @ManyToMany(mappedBy = "product")
//    private Set<OrderItem> orderItems = new HashSet<>();

    // Константы для типов изделий:
    public static final String TYPE_RING = "КОЛЬЦО";
    public static final String TYPE_EARRINGS = "СЕРЬГИ";
    public static final String TYPE_CHAIN = "ЦЕПОЧКА";
    public static final String TYPE_NECKLACE = "КОЛЬЕ";
    public static final String TYPE_BRACELET = "БРАСЛЕТ";

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

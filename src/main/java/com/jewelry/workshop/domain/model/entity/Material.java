package com.jewelry.workshop.domain.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "materials")
@Getter
@Setter
@ToString(exclude = "products")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "materials")
    private Set<Product> products = new HashSet<>();

    // Константы для часто используемых материалов
    public static final String GOLD_585 = "Золото 585";
    public static final String GOLD_750 = "Золото 750";
    public static final String SILVER_925 = "Серебро 925";
    public static final String PLATINUM_950 = "Платина 950";
    public static final String DIAMOND = "Бриллиант";
    public static final String EMERALD = "Изумруд";
    public static final String SAPPHIRE = "Сапфир";
    public static final String RUBY = "Рубин";
    public static final String PEARL = "Жемчуг";
}

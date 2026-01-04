package com.jewelry.workshop.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString()
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 256)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum Role {
        ADMIN, CLIENT, SELLER
    }

    public boolean hasRole(Role requiredRole){
        return this.role == requiredRole;
    }

    public boolean isAdmin(){
        return Role.ADMIN.equals(this.role);
    }

    public boolean isSeller(){
        return Role.SELLER.equals(this.role);
    }

    public boolean isClient(){
        return Role.CLIENT.equals(this.role);
    }


}

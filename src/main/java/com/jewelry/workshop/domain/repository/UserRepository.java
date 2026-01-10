package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Поисковые методы
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    //Поиск по ролям
    List<User> findByRole(User.Role role);
    Optional<User> findByVerificationToken(String verificationToken);
    Page<User> findByRole(User.Role role, Pageable pageable);
    List<User> findByRoleAndEnabled(User.Role role, Boolean enabled);
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    //Поиск по статусу активности
    List<User> findByEnabled(Boolean enabled);
    long countByEnabled(Boolean enabled);
    long countByRole(User.Role role);

    //Поиск по диапазону дат
    List<User> findByCreatedAtBetween(Instant start, Instant end);
    List<User> findByCreatedAtAfter(Instant date);

    //Поиск с пагинацией
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    //Поиск по почте и username
    List<User> findByEmailAndUsernameContainingAllIgnoreCase(String username, String email);

    //Запросы с @Query

    //Запросы на выборку\группировку

    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.createdAt < :date")
    List<User> findActiveUsersCreatedBefore(@Param("date") Instant date);

    @Query("SELECT u FROM User u LEFT JOIN Client c ON u.id = c.user.id WHERE c.id is NULL AND u.role = 'CLIENT'")
    List<User> findUsersWithoutClientProfile();

    @Query("SELECT u FROM User u LEFT JOIN Employee e ON u.id = e.user.id WHERE e.id IS NULL AND u.role = 'SELLER'")
    List<User> findUsersWithoutEmployeeProfile();

    @Query("""
        SELECT COUNT(u) FROM User u
        WHERE u.enabled = true
            AND u.role = :role
            AND u.createdAt BETWEEN :startDate AND :endDate
    """
    )
    Long countActiveUsersByRoleInPeriod(
            @Param("role") User.Role role,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    //Запросы на изменения
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    int updateEnabledStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);

    @Modifying
    @Query("UPDATE User u SET u.role = :role WHERE u.id = :id")
    int updateUserRole(@Param("id") Long id, @Param("role") User.Role role);

    //Данные по критериям
    @Query("""
    SELECT u FROM User u
        WHERE(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))
            AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
            AND (:role IS NULL OR u.role = :role)
            AND (:enabled IS NULL OR u.enabled = :enabled)

    """)
    Page<User> findUsersByCriteria(
            @Param("username") String username,
            @Param("email") String email,
            @Param("role") User.Role role,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}


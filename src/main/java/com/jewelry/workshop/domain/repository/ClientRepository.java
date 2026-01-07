package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Основные методы поиска
    Optional<Client> findByUserId(Long userId);
    Optional<Client> findByEmail(String email);
    List<Client> findByIsPermanent(Boolean isPermanent);
    boolean existsByEmail(String email);
    Optional<Client> findByPhone(String phone);

    // Поиск по имени
    List<Client> findByLastNameContainingIgnoreCase(String lastName);
    List<Client> findByFirstNameContainingIgnoreCase(String firstName);
    List<Client> findByLastNameAndFirstName(String lastName, String firstName);

    // Пагинация
    Page<Client> findAll(Pageable pageable);
    Page<Client> findByIsPermanent(Boolean isPermanent, Pageable pageable);
    Page<Client> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    // Статистика
    @Query("SELECT COUNT(c) FROM Client c WHERE c.isPermanent = true")
    Long countPermanentClients();

    @Query("SELECT COUNT(c) FROM Client c WHERE c.createdAt >= :date")
    Long countNewClientsSince(@Param("date") Instant date);

    // Исправленные сложные запросы
    @Query("""
        SELECT c FROM Client c 
        WHERE c.isPermanent = false 
        AND c.id IN (
            SELECT o.client.id FROM Order o 
            GROUP BY o.client.id 
            HAVING COUNT(o) >= :minOrders
        )
        """)
    List<Client> findClientsEligibleForPermanent(@Param("minOrders") Integer minOrders);

    @Query("""
        SELECT c FROM Client c 
        WHERE c.id IN (
            SELECT o.client.id FROM Order o 
            GROUP BY o.client.id 
            HAVING SUM(o.finalAmount) > (
                SELECT AVG(o2.finalAmount) FROM Order o2
            )
        )
        """)
    List<Client> findClientsWithAboveAverageSpending();

    @Query("""
        SELECT DISTINCT c FROM Client c 
        JOIN c.orders o 
        WHERE o.orderDatetime BETWEEN :startDate AND :endDate
        ORDER BY o.orderDatetime DESC
        """)
    List<Client> findClientsWithOrdersBetween(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // Новые методы по ТЗ
    @Query("""
        SELECT c FROM Client c 
        WHERE (:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
        AND (:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
        AND (:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%'))
        AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
        AND (:isPermanent IS NULL OR c.isPermanent = :isPermanent)
        AND (:minOrders IS NULL OR c.id IN (
            SELECT o.client.id FROM Order o GROUP BY o.client.id HAVING COUNT(o) >= :minOrders
        ))
        ORDER BY 
            CASE WHEN :sortBy = 'name' THEN c.lastName END ASC,
            CASE WHEN :sortBy = 'created' THEN c.createdAt END DESC,
            CASE WHEN :sortBy = 'orders' THEN (
                SELECT COUNT(o) FROM Order o WHERE o.client.id = c.id
            ) END DESC
        """)
    Page<Client> findClientsByCriteria(
            @Param("lastName") String lastName,
            @Param("firstName") String firstName,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("isPermanent") Boolean isPermanent,
            @Param("minOrders") Integer minOrders,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );

    @Query("""
        SELECT c, 
               COUNT(o) as totalOrders,
               SUM(o.finalAmount) as totalSpent,
               MAX(o.orderDatetime) as lastOrderDate
        FROM Client c 
        LEFT JOIN c.orders o 
        WHERE o.orderDatetime BETWEEN :startDate AND :endDate
        OR o.orderDatetime IS NULL
        GROUP BY c 
        HAVING totalOrders >= :minOrders OR :minOrders IS NULL
        ORDER BY totalSpent DESC NULLS LAST
        """)
    List<Object[]> getClientStatistics(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("minOrders") Integer minOrders
    );

    @Query("""
        SELECT c FROM Client c 
        WHERE c.id NOT IN (
            SELECT DISTINCT o.client.id FROM Order o 
            WHERE o.orderDatetime >= :date
        )
        AND c.createdAt < :date
        """)
    List<Client> findInactiveClients(@Param("date") Instant date);

    @Query("""
        SELECT c, 
               (SELECT COUNT(o) FROM Order o WHERE o.client.id = c.id AND o.status = 'COMPLETED') as completedOrders,
               (SELECT SUM(o.finalAmount) FROM Order o WHERE o.client.id = c.id) as lifetimeValue,
               (SELECT MAX(o.orderDatetime) FROM Order o WHERE o.client.id = c.id) as lastPurchaseDate
        FROM Client c 
        WHERE c.isPermanent = true
        ORDER BY lifetimeValue DESC
        """)
    List<Object[]> getPermanentClientDetails(Pageable pageable);

    @Query("""
        SELECT EXTRACT(MONTH FROM c.createdAt) as month,
               EXTRACT(YEAR FROM c.createdAt) as year,
               COUNT(c) as newClients,
               SUM(CASE WHEN c.isPermanent = true THEN 1 ELSE 0 END) as newPermanentClients
        FROM Client c 
        WHERE c.createdAt BETWEEN :startDate AND :endDate
        GROUP BY EXTRACT(YEAR FROM c.createdAt), EXTRACT(MONTH FROM c.createdAt)
        ORDER BY year DESC, month DESC
        """)
    List<Object[]> getClientAcquisitionReport(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // Поиск клиентов для маркетинговых кампаний
    @Query("""
        SELECT c FROM Client c 
        WHERE c.isPermanent = false 
        AND c.createdAt >= :startDate 
        AND c.id IN (
            SELECT o.client.id FROM Order o 
            WHERE o.orderDatetime BETWEEN :startDate AND :endDate
            GROUP BY o.client.id 
            HAVING SUM(o.finalAmount) BETWEEN :minAmount AND :maxAmount
        )
        """)
    List<Client> findPotentialPermanentClients(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    // Поиск клиентов с просроченными заказами
    @Query("""
        SELECT DISTINCT c FROM Client c 
        JOIN c.orders o 
        WHERE o.status NOT IN ('COMPLETED', 'CANCELLED', 'DELIVERED')
        AND o.orderDatetime < :date
        """)
    List<Client> findClientsWithOverdueOrders(@Param("date") Instant date);
}
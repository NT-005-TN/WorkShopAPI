package com.jewelry.workshop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jewelry.workshop.domain.model.entity.Client;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    //  Поисковые методы
    Optional<Client> findByEmail(String email);
    List<Client> findByIsPermanent(Boolean isPermanent);
    boolean existsByEmail(String email);
    Optional<Client> findByPhone(String phone);

    // Поиск с фильтрацией
    List<Client> findByLastNameContainingIgnoreCase(String lastName);
    List<Client> findByFirstNameContainingIgnoreCase(String firstName);
    List<Client> findByLastNameAndFirstName(String lastName, String firstName);

    // Пагинация
    Page<Client> findAll(Pageable pageable);
    Page<Client> findByIsPermanent(Boolean isPermanent, Pageable pageable);

    // Статистика
    @Query("SELECT COUNT(c) FROM Client c WHERE c.isPermanent = true")
    Long countPermanentClients();

    // Остальные запросы(более сложные)
    @Query("""
            SELECT c FROM Client c
            WHERE c.isPermanent = false
            AND c.id IN (
            SELECT o.client.id FROM Order o
            GROUP BY o.client.id
            HAVING COUNT(o) >= :minOrders
            )""")
    List<Client> findClientsEligibleForPermanent(@Param("minOrders") Integer minOrders);

    @Query("""
            SELECT c FROM Client c
            WHERE c.id IN(
            SELECT o.client.id FROM Order o
            GROUP BY o.client.id
            HAVING SUM(o.finalAmount) > (SELECT AVG(o2.finalAmount) FROM Order o2)
            )""")
    List<Client> findClientsWithAboveAverageSpending();

    @Query("""
            SELECT DISTINCT c FROM Client c
            JOIN c.orders o
            WHERE o.orderDatetime BETWEEN :startDate AND :endDate
            """)
    List<Client> findClientsWithOrdersBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
            );
}

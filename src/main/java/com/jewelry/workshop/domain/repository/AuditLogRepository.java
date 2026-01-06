package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.AuditLog;
import com.jewelry.workshop.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    //Поисковые запросы
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByUserId(Long id);

    List<AuditLog> findByAction(String action);
    List<AuditLog> findByActionIn(List<String> actions);

    List<AuditLog> findByTableName(String tableName);
    List<AuditLog> findByTableNameAcdRecordId(String tableName, Long recordId);

    List<AuditLog> findByCratedAtBetween(Instant start, Instant end);
    List<AuditLog> findByCreatedAtAfter(Instant date);
    List<AuditLog> findByCreatedAtBefore(Instant date);

    //Пагинация
    Page<AuditLog> findAll(Pageable pageable);
    Page<AuditLog> findByUser(User user, Pageable pageable);
    Page<AuditLog> findByTableName(String tableName, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);

    List<AuditLog> findByUserIdAndAction(Long userId, String action);
    List<AuditLog> findByUserIdAndTableName(Long userId, String tableName);
    List<AuditLog> findByActionAndTableName(String action, String tableName);

    //Запросы с Query
    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.user.id = :userId
            AND a.tableName = :tableName
            AND a.createdAt >= :sinceDate
            ORDER BY a.createdAt DESC
            """)
    List<AuditLog> findRecentUserActionsInTable(
            @Param("userId") Long userId,
            @Param("tableName") String tableName,
            @Param("sinceDate") Instant sinceDate
    );

    @Query("""
            SELECT a.tableName, COUNT(a) as actionCount
            FROM AuditLog a
            WHERE a.createdAt BETWEEN :startDate AND :endDate
            GROUP BY a.tableName
            ORDER BY actionCount DESC
            """)
    List<Object[]> countActionsByTableInPeriod(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
        SELECT a.user.id, u.username,
}

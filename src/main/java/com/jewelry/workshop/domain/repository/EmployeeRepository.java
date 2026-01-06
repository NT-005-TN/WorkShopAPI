package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.Employee;
import com.jewelry.workshop.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //Поисковые запросы
    Optional<Employee> findByUser(User user);

    Optional<Employee> findByUserId(Long userId);

    List<Employee> findByDepartment(String department);

    List<Employee> findByPosition(String position);

    List<Employee> findByDepartmentAndPosition(String department, String position);

    List<Employee> findByDepartmentContainingIgnoreCase(String department);

    List<Employee> findByPositionContainingIgnoreCase(String position);


    List<Employee> findByCreatedAtBetween(Instant start, Instant end);

    List<Employee> findByCreatedAtAfter(Instant date);

    //Пагинация
    Page<Employee> findAll(Pageable pageable);

    Page<Employee> findByDepartment(String department, Pageable pageable);

    Page<Employee> findByPosition(String position, Pageable pageable);

    boolean existsByYserId(Long userId);

    //Запросы с Query - выборка
    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.deaprtment")
    List<Object[]> countEmployeesByDepartment();

    @Query("SELECT e.position, COUNT(e) FROM Employee e GROUP BY e.position")
    List<Object[]> countEmployeesByPosition();

    @Query("""
        SELECT e FROM Employee e
        JOIN e.user u
        WHERE u.role = :role
    """)
    List<Employee> findByUserRole(@Param("role") User.Role role);


    @Query("""
        SELECT e FROM Employee e
            WHERE e.department = : department
            AND e.id IN (
                SELECT a.user.id FROM AuditLog a
                    WHERE a.action = 'CREATE'
                    AND a.createdAt >= :sinceDate
                )
    """)
    List<Employee> findDepartmentEmployeesWithRecentActivity(
            @Param("department") String department,
            @Param("sinceDate") Instant sinceDate
    );

    @Query("""
        SELECT e FROM Employee e 
        WHERE (:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%')))
        AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
        """)
    Page<Employee> findEmployeesByCriteria(
            @Param("department") String department,
            @Param("position") String position,
            Pageable pageable
    );

}



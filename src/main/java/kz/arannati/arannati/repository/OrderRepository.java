package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    @RestResource(path = "by-user-list", rel = "by-user-list")
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @RestResource(path = "by-user-page", rel = "by-user-page")
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Order> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:userId IS NULL OR o.user.id = :userId) AND " +
            "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<Order> findOrdersWithFilters(
            @Param("status") String status,
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> searchOrders(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    long countOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND " +
            "o.createdAt >= :startDate AND o.createdAt <= :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    long countByStatus(String status);

    long countByUserId(Long userId);
}

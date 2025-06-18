package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Order> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findOrdersWithFilters(
            String status,
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    Page<Order> searchOrders(String search, Pageable pageable);

    long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(String status);

    long countByUserId(Long userId);
    
    Order save(Order order);
    
    Optional<Order> findById(Long id);
    
    void deleteById(Long id);
    
    List<Order> findAll();
}
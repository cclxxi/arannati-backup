package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Optional<OrderDTO> findByOrderNumber(String orderNumber);

    Page<OrderDTO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<OrderDTO> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<OrderDTO> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<OrderDTO> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<OrderDTO> findOrdersWithFilters(
            String status,
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    Page<OrderDTO> searchOrders(String search, Pageable pageable);

    long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(String status);

    long countByUserId(Long userId);

    OrderDTO save(OrderDTO orderDTO);

    Optional<OrderDTO> findById(Long id);

    void deleteById(Long id);

    List<OrderDTO> findAll();

    OrderDTO convertToDto(Order order);

    Order convertToEntity(OrderDTO orderDTO);

    OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Long userId);
    Page<OrderDTO> findAllOrderByCreatedAtDesc(Pageable pageable);
    List<OrderDTO> findByStatus(String status);
    OrderDTO updateOrderStatus(Long orderId, String status);
    OrderDTO cancelOrder(Long orderId);
    List<OrderDTO> findRecentOrders(int limit);
}

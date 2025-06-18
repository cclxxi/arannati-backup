package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    List<Object[]> findPopularProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    void deleteByOrderId(Long orderId);
    
    OrderItem save(OrderItem orderItem);
    
    Optional<OrderItem> findById(Long id);
    
    void deleteById(Long id);
    
    List<OrderItem> findAll();
}
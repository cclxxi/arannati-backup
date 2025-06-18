package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.OrderItemDTO;
import kz.arannati.arannati.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    List<OrderItemDTO> findByOrderId(Long orderId);

    List<OrderItemDTO> findByProductId(Long productId);

    List<Object[]> findPopularProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    void deleteByOrderId(Long orderId);

    OrderItemDTO save(OrderItemDTO orderItemDTO);

    Optional<OrderItemDTO> findById(Long id);

    void deleteById(Long id);

    List<OrderItemDTO> findAll();

    OrderItemDTO convertToDto(OrderItem orderItem);

    OrderItem convertToEntity(OrderItemDTO orderItemDTO);
}

package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.OrderItemDTO;
import kz.arannati.arannati.entity.Order;
import kz.arannati.arannati.entity.OrderItem;
import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.repository.OrderItemRepository;
import kz.arannati.arannati.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderItemDTO convertToDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO.OrderItemDTOBuilder builder = OrderItemDTO.builder()
                .id(orderItem.getId())
                .productName(orderItem.getProductName())
                .productSku(orderItem.getProductSku())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .discountAmount(orderItem.getDiscountAmount())
                .createdAt(orderItem.getCreatedAt());

        if (orderItem.getOrder() != null) {
            builder.orderId(orderItem.getOrder().getId());
        }

        if (orderItem.getProduct() != null) {
            builder.productId(orderItem.getProduct().getId());
        }

        return builder.build();
    }

    @Override
    public OrderItem convertToEntity(OrderItemDTO orderItemDTO) {
        if (orderItemDTO == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemDTO.getId());
        orderItem.setProductName(orderItemDTO.getProductName());
        orderItem.setProductSku(orderItemDTO.getProductSku());
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setUnitPrice(orderItemDTO.getUnitPrice());
        orderItem.setTotalPrice(orderItemDTO.getTotalPrice());
        orderItem.setDiscountAmount(orderItemDTO.getDiscountAmount());
        orderItem.setCreatedAt(orderItemDTO.getCreatedAt());

        // Order and Product relationships are typically handled separately
        // as they require fetching the actual entities from the database

        return orderItem;
    }

    @Override
    public List<OrderItemDTO> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDTO> findByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> findPopularProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderItemRepository.findPopularProductsByDateRange(startDate, endDate);
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        orderItemRepository.deleteByOrderId(orderId);
    }

    @Override
    public OrderItemDTO save(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = convertToEntity(orderItemDTO);

        // Handle order relationship if orderId is provided
        if (orderItemDTO.getOrderId() != null) {
            Order order = new Order();
            order.setId(orderItemDTO.getOrderId());
            orderItem.setOrder(order);
        }

        // Handle product relationship if productId is provided
        if (orderItemDTO.getProductId() != null) {
            Product product = new Product();
            product.setId(orderItemDTO.getProductId());
            orderItem.setProduct(product);
        }

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return convertToDto(savedOrderItem);
    }

    @Override
    public Optional<OrderItemDTO> findById(Long id) {
        return orderItemRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    public List<OrderItemDTO> findAll() {
        return orderItemRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.OrderCreateDTO;
import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.entity.Order;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.OrderRepository;
import kz.arannati.arannati.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderDTO convertToDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO.OrderDTOBuilder builder = OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .shippingAmount(order.getShippingAmount())
                .taxAmount(order.getTaxAmount())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryMethod(order.getDeliveryMethod())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        if (order.getUser() != null) {
            builder.userId(order.getUser().getId());
        }

        return builder.build();
    }

    @Override
    public Order convertToEntity(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }

        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setOrderNumber(orderDTO.getOrderNumber());
        order.setStatus(orderDTO.getStatus());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDiscountAmount(orderDTO.getDiscountAmount());
        order.setShippingAmount(orderDTO.getShippingAmount());
        order.setTaxAmount(orderDTO.getTaxAmount());
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setCustomerPhone(orderDTO.getCustomerPhone());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setDeliveryMethod(orderDTO.getDeliveryMethod());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setPaymentStatus(orderDTO.getPaymentStatus());
        order.setNotes(orderDTO.getNotes());
        order.setCreatedAt(orderDTO.getCreatedAt());
        order.setUpdatedAt(orderDTO.getUpdatedAt());

        // User relationship is typically handled separately
        // as it requires fetching the actual entity from the database

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findOrdersWithFilters(String status, Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findOrdersWithFilters(status, userId, startDate, endDate, pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> searchOrders(String search, Pageable pageable) {
        Page<Order> orderPage = orderRepository.searchOrders(search, pageable);
        List<OrderDTO> orderDTOs = orderPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countOrdersByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTotalRevenueByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        Order order = convertToEntity(orderDTO);

        // Handle user relationship if userId is provided
        if (orderDTO.getUserId() != null) {
            User user = new User();
            user.setId(orderDTO.getUserId());
            order.setUser(user);
        }

        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Long userId) {
        // Implementation of createOrder method
        // This is a placeholder implementation
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllOrderByCreatedAtDesc(Pageable pageable) {
        // Implementation of findAllOrderByCreatedAtDesc method
        // This is a placeholder implementation
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findByStatus(String status) {
        // Implementation of findByStatus method
        // This is a placeholder implementation
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByStatusWithPagination(String status, Pageable pageable) {
        return findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        // Implementation of updateOrderStatus method
        // This is a placeholder implementation
        return null;
    }

    @Override
    public OrderDTO cancelOrder(Long orderId) {
        // Implementation of cancelOrder method
        // This is a placeholder implementation
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findRecentOrders(int limit) {
        // Implementation of findRecentOrders method
        // This is a placeholder implementation
        return null;
    }
}

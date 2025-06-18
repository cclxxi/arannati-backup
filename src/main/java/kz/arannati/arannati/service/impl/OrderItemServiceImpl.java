package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.OrderItem;
import kz.arannati.arannati.repository.OrderItemRepository;
import kz.arannati.arannati.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return null;
    }

    @Override
    public List<OrderItem> findByProductId(Long productId) {
        return null;
    }

    @Override
    public List<Object[]> findPopularProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        return null;
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<OrderItem> findAll() {
        return null;
    }
}
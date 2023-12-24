package com.example.ordersservice.service;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.CreateOrderDto;
import com.example.ordersservice.messaging.events.OrderCreationEvent;
import com.example.ordersservice.messaging.events.UpdateOrderStatusEvent;
import com.example.ordersservice.messaging.producer.OrderEventProducer;
import com.example.ordersservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.ordersservice.domain.OrderStatus.CREATED;
import static com.example.ordersservice.domain.OrderStatus.DELIVERED;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional(readOnly = true)
    public Order get(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order with id " + id + " not exists"));
    }

    @Transactional
    public Order create(CreateOrderDto request) {
        Order order = Order.builder()
                .product(request.product())
                .category(request.category())
                .customerName(request.customerName())
                .price(request.price())
                .deliveryDestination(request.destination())
                .status(CREATED)
                .isCompleted(false)
                .build();
        Order savedOrder = orderRepository.save(order);
        OrderCreationEvent event = buildOrderCreationEvent(savedOrder);
        orderEventProducer.sendOrderCreationEvent(event);
        return savedOrder;
    }

    @Transactional
    public void updateStatus(UpdateOrderStatusEvent event) {
        Order order = orderRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus status = switch (event.getStatus()) {
            case ON_THE_WAY -> OrderStatus.ON_THE_WAY;
            case DELIVERED -> OrderStatus.DELIVERED;
            default -> throw new RuntimeException("Trying to pass unexpected order status");
        };
        order.setStatus(status);
        if (DELIVERED.equals(status)) {
            order.setCompleted(true);
            order.setCompletedAt(LocalDateTime.now());
        }
        orderRepository.save(order);
    }

    private OrderCreationEvent buildOrderCreationEvent(Order order) {
        return OrderCreationEvent.builder()
                .id(order.getId())
                .product(order.getProduct())
                .category(order.getCategory())
                .price(order.getPrice())
                .customerName(order.getCustomerName())
                .deliveryDestination(order.getDeliveryDestination())
                .status(order.getStatus())
                .isCompleted(order.isCompleted())
                .completedAt(order.getCompletedAt())
                .build();
    }

}

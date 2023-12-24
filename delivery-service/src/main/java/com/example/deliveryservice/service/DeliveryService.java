package com.example.deliveryservice.service;

import com.example.deliveryservice.domain.Delivery;
import com.example.deliveryservice.messaging.events.OrderCreationEvent;
import com.example.deliveryservice.messaging.events.OrderStatus;
import com.example.deliveryservice.messaging.events.UpdateOrderStatusEvent;
import com.example.deliveryservice.messaging.producer.UpdateOrderStatusEventProducer;
import com.example.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.example.deliveryservice.domain.DeliveryStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UpdateOrderStatusEventProducer updateOrderStatusEventProducer;

    private static final int DELIVERY_PREPARATION_DURATION_MS = 15000;
    private static final int DELIVERY_SHIPPING_DURATION_MS = 10000;

    @Transactional
    public void startDelivery(OrderCreationEvent event) {
        validateDestination(event.getDeliveryDestination());
        Delivery delivery = Delivery.builder()
                .orderId(event.getId())
                .product(event.getProduct())
                .customerName(event.getCustomerName())
                .destination(event.getDeliveryDestination())
                .status(PREPARATION)
                .createdAt(LocalDateTime.now())
                .build();
        deliveryRepository.save(delivery);
    }

    @Transactional
    @Scheduled(fixedRate = DELIVERY_PREPARATION_DURATION_MS)
    public void startShipping() {

        List<Delivery> deliveries = StreamSupport.stream(deliveryRepository.findAll().spliterator(), false)
                .filter(delivery -> PREPARATION.equals(delivery.getStatus()))
                .toList();
        deliveries.forEach(delivery -> {
            delivery.setStatus(ON_THE_WAY);
            deliveryRepository.save(delivery);
            UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(delivery.getOrderId(), OrderStatus.ON_THE_WAY);
            updateOrderStatusEventProducer.sendEvent(event);
        });
    }

    @Transactional
    @Scheduled(fixedRate = DELIVERY_SHIPPING_DURATION_MS)
    public void completeShipping() {
        List<Delivery> deliveries = StreamSupport.stream(deliveryRepository.findAll().spliterator(), false)
                .filter(delivery -> ON_THE_WAY.equals(delivery.getStatus()))
                .toList();
        deliveries.forEach(delivery -> {
            delivery.setStatus(DELIVERED);
            deliveryRepository.save(delivery);
            UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(delivery.getOrderId(), OrderStatus.DELIVERED);
            updateOrderStatusEventProducer.sendEvent(event);
        });
    }

    private void validateDestination(String destination) {
        if (destination.isBlank()) {
            log.warn("Wrong delivery destination");
            throw new IllegalArgumentException("Wrong delivery destination");
        }
    }

}

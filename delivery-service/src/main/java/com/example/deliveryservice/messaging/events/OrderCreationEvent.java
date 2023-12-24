package com.example.deliveryservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreationEvent {
    Long id;
    String product;
    String category;
    BigDecimal price;
    String customerName;
    String deliveryDestination;
    OrderStatus status;
    Boolean isCompleted;
    LocalDateTime completedAt;
}


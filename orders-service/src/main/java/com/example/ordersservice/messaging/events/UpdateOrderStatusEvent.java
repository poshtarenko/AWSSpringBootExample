package com.example.ordersservice.messaging.events;

import com.example.ordersservice.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusEvent {
    String id;
    OrderStatus status;
}

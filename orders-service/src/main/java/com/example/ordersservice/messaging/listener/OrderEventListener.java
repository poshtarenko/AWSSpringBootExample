package com.example.ordersservice.messaging.listener;

import com.example.ordersservice.messaging.events.UpdateOrderStatusEvent;
import com.example.ordersservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @SqsListener("${queues.order-update-status.name}")
    @SneakyThrows
    public void onOrderUpdateStatusEvent(String message) {
        UpdateOrderStatusEvent event = objectMapper.readValue(message, UpdateOrderStatusEvent.class);
        log.info("Got message : " + message);
        orderService.updateStatus(event);
    }

}

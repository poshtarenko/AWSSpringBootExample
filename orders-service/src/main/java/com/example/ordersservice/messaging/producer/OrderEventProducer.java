package com.example.ordersservice.messaging.producer;

import com.example.ordersservice.messaging.events.OrderCreationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${queues.order-creation-delivery.url}")
    private String deliveryQueueUrl;
    @Value("${queues.order-creation-reports.url}")
    private String reportsQueueUrl;

    @SneakyThrows
    public void sendOrderCreationEvent(OrderCreationEvent orderEvent) {
        log.info("Sending order creation event : " + orderEvent);
        String msg = objectMapper.writeValueAsString(orderEvent);
        sqsTemplate.send(deliveryQueueUrl, msg);
        sqsTemplate.send(reportsQueueUrl, msg);
    }

}

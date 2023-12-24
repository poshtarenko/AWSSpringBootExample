package com.example.deliveryservice.messaging.producer;

import com.example.deliveryservice.messaging.events.UpdateOrderStatusEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderStatusEventProducer {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${queues.order-update-status.url}")
    private String queueUrl;

    @Transactional
    public void sendEvent(UpdateOrderStatusEvent orderEvent) {
        sendEvent(queueUrl, orderEvent);
    }

    @Transactional
    @SneakyThrows
    public void sendEvent(String queue, UpdateOrderStatusEvent event) {
        log.info("Sending update order status event : " + event);
        String msg = objectMapper.writeValueAsString(event);
        sqsTemplate.send(queue, msg);
    }

}

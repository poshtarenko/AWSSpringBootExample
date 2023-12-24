package com.example.reportsservice.messaging.listener;

import com.example.reportsservice.messaging.events.OrderCreationEvent;
import com.example.reportsservice.service.ProductSalesReportService;
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
    private final ProductSalesReportService productSalesReportService;

    @SqsListener("${queues.order-creation-reports.name}")
    @SneakyThrows
    public void onOrderCreatedEvent(String message) {
        OrderCreationEvent event = objectMapper.readValue(message, OrderCreationEvent.class);
        log.info("Got message : " + message);
        productSalesReportService.updateReport(event);
    }

}

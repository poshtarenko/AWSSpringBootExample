package com.example.deliveryservice.repository;

import com.example.deliveryservice.domain.Delivery;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface DeliveryRepository extends CrudRepository<Delivery, String> {
}

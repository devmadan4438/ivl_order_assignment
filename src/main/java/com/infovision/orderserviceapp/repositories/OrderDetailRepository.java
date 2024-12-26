package com.infovision.orderserviceapp.repositories;

import com.infovision.orderserviceapp.entities.OrderDetails;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderDetailRepository extends ReactiveCrudRepository<OrderDetails,Long> {
}

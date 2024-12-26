package com.infovision.orderserviceapp.repositories;

import com.infovision.orderserviceapp.dto.CreateOrderDTO;
import com.infovision.orderserviceapp.dto.OrderWithDetailsDTO;
import com.infovision.orderserviceapp.entities.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("""
        SELECT o.id, o.order_no, o.order_status, o.sub_total, o.tax_amount, o.net_amount,
               o.created_by, o.created_on, o.updated_by, o.updated_on, o.deleted_by, o.deleted_on, o.is_deleted,
               od.item_name, od.length, od.breadth, od.height, od.size, od.price, od.tax_per, od.tax_value, od.uom
        FROM orders o
        LEFT JOIN order_details od ON o.id = od.order_id
        WHERE o.created_by = :username
       """)
    Flux<OrderWithDetailsDTO> findOrderByCreatedBy(@Param("username") String username);

}

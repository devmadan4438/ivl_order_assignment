package com.infovision.orderserviceapp.services;

import com.infovision.orderserviceapp.constants.OrderStatus;
import com.infovision.orderserviceapp.constants.SequenceNames;
import com.infovision.orderserviceapp.dto.CreateOrderDTO;
import com.infovision.orderserviceapp.dto.OrderWithDetailsDTO;
import com.infovision.orderserviceapp.entities.Order;
import com.infovision.orderserviceapp.entities.OrderDetails;
import com.infovision.orderserviceapp.entities.SequenceGenerator;
import com.infovision.orderserviceapp.repositories.OrderDetailRepository;
import com.infovision.orderserviceapp.repositories.OrderRepository;
import com.infovision.orderserviceapp.repositories.SequenceGeneratorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final SequenceGeneratorRepository sequenceGeneratorRepository;

    private final DatabaseClient databaseClient;

    public OrderService(OrderDetailRepository orderDetailRepository, OrderRepository orderRepository, SequenceGeneratorRepository sequenceGeneratorRepository,DatabaseClient databaseClient) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.sequenceGeneratorRepository  = sequenceGeneratorRepository;
        this.databaseClient = databaseClient;
    }

    // Start service logic

    // Function to create orders
    public Mono<Order> createOrder(CreateOrderDTO createOrderBody) {
        int currentYear = LocalDate.now().getYear();

        // Fetch or initialize the counter for the current year
        Mono<SequenceGenerator> counterMono = sequenceGeneratorRepository.findByYear(currentYear)
                .switchIfEmpty(Mono.defer(() -> {
                    SequenceGenerator newCounter = new SequenceGenerator();
                    newCounter.setSequenceName(String.valueOf(SequenceNames.Order));
                    newCounter.setYear(currentYear);
                    newCounter.setCounter(0);
                    return sequenceGeneratorRepository.save(newCounter);
                }));

        // Generate order number
        return counterMono.flatMap(counter -> {
            counter.setCounter(counter.getCounter() + 1);

            // Save the updated counter
            return sequenceGeneratorRepository.save(counter)
                    .flatMap(updatedCounter -> {

                        String orderNumber = String.format("%d-%08d", currentYear, updatedCounter.getCounter());

                        // Create the Order object
                        Order order = new Order();
                        order.setOrderNo(orderNumber);
                        order.setOrderStatus(String.valueOf(OrderStatus.Pending));

                        float subTotal = calculateSubTotal(createOrderBody.getItems());
                        float taxAmount = calculateTaxAmount(createOrderBody.getItems());

                        order.setSubTotal(subTotal);
                        order.setTaxAmount(taxAmount);
                        order.setNetAmount(subTotal + taxAmount);

                        order.setCreatedBy(createOrderBody.getCreatedBy());
                        order.setCreatedOn(LocalDateTime.now());
                        order.setUpdatedOn(LocalDateTime.now());

                        // Save the order
                        return orderRepository.save(order).flatMap(savedOrder -> {
                            // Prepare OrderDetails
                            List<OrderDetails> orderDetailsList = getOrderDetails(createOrderBody);
                            orderDetailsList.forEach(orderDetails -> orderDetails.setOrderId(savedOrder.getId()));

                            // Save all order details and return the saved order
                            return orderDetailRepository.saveAll(orderDetailsList)
                                    .then(Mono.just(savedOrder));
                        });
                    });
        });
    }

    // Function to get order listing
    public Flux<OrderWithDetailsDTO> getOrderListing(String username, String status, Long orderId) {
        StringBuilder query = new StringBuilder("""
        SELECT 
            o.id AS order_id,
            o.order_no AS order_no,
            o.created_by AS created_by,
            o.created_on AS created_on,
            o.order_status AS order_status,
            od.id AS order_detail_id,
            od.item_name AS item_name,
            od.price AS price
        FROM orders o
        LEFT JOIN order_details od ON o.id = od.order_id
        WHERE o.created_by = :username
    """);

        // Conditionally filter
        if (orderId != null) {
            query.append(" AND o.id = :order_id");
        }
        if (status != null && !status.isEmpty()) {
            query.append(" AND o.order_status = :status");
        }

        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(query.toString())
                .bind("username", username);

        // Bind if it's provided
        if (orderId != null) {
            spec = spec.bind("order_id", orderId);
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.bind("status", status);
        }
        return spec.map((row, meta) -> {
            OrderWithDetailsDTO order = new OrderWithDetailsDTO();
            order.setId(row.get("order_id", Long.class));
            order.setOrderNo(row.get("order_no", String.class));
            order.setCreatedBy(row.get("created_by", String.class));
            order.setCreatedOn(row.get("created_on", LocalDateTime.class));
            order.setOrderStatus(row.get("order_status", String.class));

            // Check if order details exist
            Long detailId = row.get("order_detail_id", Long.class);
            if (detailId != null) {
                OrderWithDetailsDTO.Item item = OrderWithDetailsDTO.Item.builder()
                        .id(detailId)
                        .orderId(row.get("order_id", Long.class))
                        .name(row.get("item_name", String.class))
                        .price(row.get("price", Float.class)) // Ensure price is handled gracefully
                        .build();

                order.setOrderDetails(Collections.singletonList(item));
            } else {
                // Set empty list if no details found
                order.setOrderDetails(Collections.emptyList());
            }

            return order;
        }).all();
    }

    // Function to update order status
    public Mono<Order> updateStatus(Long id, String status) {
        return orderRepository.findById(id).flatMap(order -> {
            order.setOrderStatus(status);
            return orderRepository.save(order);
        });
    }

    // Function to delete the order items
    public Mono<String> removeOrderItems(Long orderDetailId) {
        return orderDetailRepository.findById(orderDetailId)
                .flatMap(orderDetails -> orderDetailRepository.deleteById(orderDetailId)
                       .then(Mono.just("Deleted successfully")))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("OrderDetail not found with id: " + orderDetailId)));
    }


    // Helper function
    private static List<OrderDetails> getOrderDetails(CreateOrderDTO createOrderBody) {
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        for (CreateOrderDTO.Item orderDetailDTO : createOrderBody.getItems()) {
            OrderDetails orderDetails = new OrderDetails();

            // Set order details fields
            orderDetails.setItemName(orderDetailDTO.getName());
            orderDetails.setSize(orderDetailDTO.getSize());
            orderDetails.setPrice(orderDetailDTO.getPrice());
            orderDetails.setTaxPer(orderDetailDTO.getTaxPer());
            orderDetails.setTaxValue(orderDetailDTO.getTaxValue());
            orderDetails.setLength(orderDetailDTO.getLength());
            orderDetails.setBreadth(orderDetailDTO.getBreadth());
            orderDetails.setHeight(orderDetailDTO.getHeight());
            orderDetails.setUom(orderDetailDTO.getUom());

            // Add to the list
            orderDetailsList.add(orderDetails);
        }
        return orderDetailsList;
    }

    private static float calculateSubTotal(List<CreateOrderDTO.Item> orderDetailDTO) {
        float totalAmount = 0;
          for(CreateOrderDTO.Item orderDetailDTOItem : orderDetailDTO) {
              totalAmount += orderDetailDTOItem.getPrice();
          }
        return totalAmount;
    }

    private static float calculateTaxAmount(List<CreateOrderDTO.Item> orderDetailDTO) {
        float totalTax = 0;
        for(CreateOrderDTO.Item orderDetailDTOItem : orderDetailDTO) {
            totalTax += orderDetailDTOItem.getTaxValue();
        }
        return totalTax;
    }

}



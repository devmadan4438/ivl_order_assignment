package com.infovision.orderserviceapp.controllers;

import com.infovision.orderserviceapp.dto.CreateOrderDTO;
import com.infovision.orderserviceapp.dto.OrderWithDetailsDTO;
import com.infovision.orderserviceapp.entities.Order;
import com.infovision.orderserviceapp.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Mono<ResponseEntity<Order>> create(@RequestBody CreateOrderDTO orderBody) {
        return orderService.createOrder(orderBody)
                .map(order -> new ResponseEntity<>(order, HttpStatus.CREATED))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @GetMapping("/{username}")
    public Flux<OrderWithDetailsDTO> getAllOrders(
            @PathVariable String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long orderId) {
        return orderService.getOrderListing(username, status, orderId);
    }

    @PatchMapping("/{orderId}")
    public Mono<Order> updateOrderStatus(@PathVariable Long orderId,@RequestParam String status) {
        return orderService.updateStatus(orderId,status);
    }

    @DeleteMapping("/{orderDetailId}")
    public Mono<String> removeOrderItems(@PathVariable Long orderDetailId) {
        return orderService.removeOrderItems(orderDetailId);
    }
}

package com.agriconnect.controller;

import com.agriconnect.dto.CheckoutRequest;
import com.agriconnect.dto.OrderDto;
import com.agriconnect.dto.OrderItemDto;
import com.agriconnect.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping({"", "/checkout"})
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
        try {
            OrderDto order = orderService.checkout(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping({"/customer/{customerId}", "/user/{customerId}"})
    public ResponseEntity<?> getCustomerOrders(@PathVariable Long customerId) {
        try {
            List<OrderDto> orders = orderService.getCustomerOrders(customerId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id, @RequestParam(required = false) Long customerId) {
        try {
            OrderDto order = orderService.getOrderById(id, customerId);
            return ResponseEntity.ok(order);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/track")
    public ResponseEntity<?> trackOrder(@PathVariable Long id, @RequestParam(required = false) Long customerId) {
        try {
            OrderDto order = orderService.getOrderById(id, customerId);
            return ResponseEntity.ok(Map.of(
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "paymentStatus", order.getPaymentStatus(),
                    "totalAmount", order.getTotalAmount(),
                    "deliveryAddress", order.getDeliveryAddress(),
                    "createdAt", order.getCreatedAt()
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<?> getOrderItems(@PathVariable Long id, @RequestParam(required = false) Long customerId) {
        try {
            OrderDto order = orderService.getOrderById(id, customerId);
            return ResponseEntity.ok(order.getItems());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/farmer/{farmerId}")
    public List<OrderDto> getFarmerOrders(@PathVariable Long farmerId) {
        return orderService.getFarmerOrders(farmerId);
    }

  
@PutMapping("/{id}/status")
public ResponseEntity<?> updateStatus(
        @PathVariable Long id,
        @RequestParam Long farmerId,
        @RequestBody Map<String, String> req) {

    try {
        String newStatus = req.get("status");

        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Status is required."));
        }

        OrderDto updated = orderService.updateFarmerOrderStatus(
                id,
                farmerId,
                newStatus
        );

        return ResponseEntity.ok(updated);

    } catch (SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));

    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
    }
}


    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestParam(required = false) Long customerId) {
        try {
            OrderDto cancelled = orderService.cancelOrder(id, customerId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Order #" + id + " cancelled successfully. Stock has been restored.",
                    "order", cancelled
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

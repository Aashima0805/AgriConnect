
package com.agriconnect.service;

import com.agriconnect.dto.CheckoutRequest;
import com.agriconnect.dto.OrderDto;
import com.agriconnect.dto.OrderItemDto;
import com.agriconnect.entity.*;
import com.agriconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CartItemRepository cartRepo;
    private final NotificationRepository notificationRepo;

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository orderItemRepo,
                        ProductRepository productRepo,
                        UserRepository userRepo,
                        CartItemRepository cartRepo,
                        NotificationRepository notificationRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.notificationRepo = notificationRepo;
    }

    @Transactional
    public OrderDto checkout(CheckoutRequest req) {
        if (req == null || req.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }

        User customer = userRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + req.getCustomerId()));

        List<CartItem> cartItems = new ArrayList<>();
        List<CheckoutRequest.DirectOrderItemRequest> requestedItems = req.getItems();

        if (requestedItems != null && !requestedItems.isEmpty()) {
            for (CheckoutRequest.DirectOrderItemRequest dir : requestedItems) {
                Product p = productRepo.findById(dir.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Product not found with ID: " + dir.getProductId()));

                cartItems.add(new CartItem(customer, p, dir.getQuantity()));
            }
        } else {
            cartItems = cartRepo.findByCustomerId(customer.getId());
        }

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout: Cart is empty.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> itemsToSave = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product product = ci.getProduct();

            Product freshProduct = productRepo.findById(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: " + product.getName()));

            double requestedQty = ci.getQuantity();

            if (requestedQty <= 0) {
                throw new IllegalArgumentException(
                        "Invalid order quantity for product: " + freshProduct.getName());
            }

            if (!freshProduct.isAvailable() ||
                    freshProduct.getQuantity() < requestedQty) {

                throw new IllegalArgumentException(
                        "Insufficient stock for '" + freshProduct.getName() +
                                "'. Requested: " + requestedQty +
                                ", Available: " + freshProduct.getQuantity());
            }

            BigDecimal unitPrice = freshProduct.getPrice();
            BigDecimal subtotal =
                    unitPrice.multiply(BigDecimal.valueOf(requestedQty));

            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(freshProduct);
            orderItem.setQuantity(requestedQty);
            orderItem.setUnitPrice(unitPrice);

            itemsToSave.add(orderItem);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PLACED);

        order.setDeliveryAddress(
                req.getDeliveryAddress() != null &&
                        !req.getDeliveryAddress().isBlank()
                        ? req.getDeliveryAddress().trim()
                        : (customer.getAddress() != null
                        ? customer.getAddress()
                        : "Standard Delivery Address")
        );

        order.setDeliveryType(
                req.getDeliveryType() != null
                        ? req.getDeliveryType()
                        : "Standard Delivery"
        );

        order.setPaymentStatus("PENDING");

        Order savedOrder = orderRepo.save(order);

        Set<User> farmersToNotify = new HashSet<>();
        List<OrderItemDto> savedItemDtos = new ArrayList<>();

        for (OrderItem item : itemsToSave) {
            item.setOrder(savedOrder);

            OrderItem savedItem = orderItemRepo.save(item);
            savedItemDtos.add(OrderItemDto.fromEntity(savedItem));

            Product product = item.getProduct();

            double remainingQty =
                    product.getQuantity() - item.getQuantity();

            product.setQuantity(Math.max(0, remainingQty));

            if (remainingQty <= 0) {
                product.setAvailable(false);
            }

            productRepo.save(product);

            if (product.getFarmer() != null) {
                farmersToNotify.add(product.getFarmer());
            }
        }

        cartRepo.deleteByCustomerId(customer.getId());

        for (User farmer : farmersToNotify) {
            notificationRepo.save(new Notification(
                    farmer,
                    "New Purchase Order #" + savedOrder.getId(),
                    "New agricultural produce order #" +
                            savedOrder.getId() +
                            " placed by " +
                            customer.getName() +
                            " for total ₹" +
                            totalAmount
            ));
        }

        OrderDto orderDto = OrderDto.fromEntity(savedOrder);
        orderDto.setItems(savedItemDtos);

        return orderDto;
    }

    public List<OrderDto> getCustomerOrders(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }

        List<Order> orders =
                orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId);

        return orders.stream().map(order -> {
            OrderDto dto = OrderDto.fromEntity(order);

            List<OrderItemDto> items =
                    orderItemRepo.findByOrderId(order.getId())
                            .stream()
                            .map(OrderItemDto::fromEntity)
                            .collect(Collectors.toList());

            dto.setItems(items);

            return dto;
        }).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long orderId, Long requestingCustomerId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required.");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));

        if (requestingCustomerId != null &&
                !order.getCustomer().getId().equals(requestingCustomerId)) {

            throw new SecurityException(
                    "Unauthorized: You can only view details of your own orders.");
        }

        OrderDto dto = OrderDto.fromEntity(order);

        List<OrderItemDto> items =
                orderItemRepo.findByOrderId(order.getId())
                        .stream()
                        .map(OrderItemDto::fromEntity)
                        .collect(Collectors.toList());

        dto.setItems(items);

        return dto;
    }

    public List<OrderDto> getFarmerOrders(Long farmerId) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }

        List<Order> orders =
                orderRepo.findOrdersByFarmerId(farmerId);

        return orders.stream().map(order -> {
            OrderDto dto = OrderDto.fromEntity(order);

            List<OrderItemDto> items =
                    orderItemRepo.findByOrderId(order.getId())
                            .stream()
                            .map(OrderItemDto::fromEntity)
                            .collect(Collectors.toList());

            dto.setItems(items);

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateFarmerOrderStatus(
            Long orderId,
            Long farmerId,
            String newStatusStr) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required.");
        }

        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }

        User farmer = userRepo.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Farmer not found with ID: " + farmerId));

        if (farmer.getRole() != User.Role.FARMER) {
            throw new SecurityException(
                    "Only farmers can update produce order status.");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));

        List<OrderItem> items =
                orderItemRepo.findByOrderId(orderId);

        boolean farmerOwnsOrder = items.stream()
                .anyMatch(item ->
                        item.getProduct() != null &&
                        item.getProduct().getFarmer() != null &&
                        item.getProduct().getFarmer().getId().equals(farmerId)
                );

        if (!farmerOwnsOrder) {
            throw new SecurityException(
                    "You are not authorized to update this order.");
        }

        if (newStatusStr == null || newStatusStr.isBlank()) {
            throw new IllegalArgumentException(
                    "Status is required.");
        }

        Order.OrderStatus newStatus;

        try {
            newStatus = Order.OrderStatus.valueOf(
                    newStatusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid order status: " + newStatusStr);
        }

        Order.OrderStatus currentStatus = order.getStatus();

        if (currentStatus == Order.OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled orders cannot be updated.");
        }

        if (currentStatus == Order.OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                    "Delivered orders cannot be updated.");
        }

        Order.OrderStatus expectedNextStatus;

        switch (currentStatus) {
            case PLACED:
                expectedNextStatus = Order.OrderStatus.CONFIRMED;
                break;

            case CONFIRMED:
                expectedNextStatus = Order.OrderStatus.PACKED;
                break;

            case PACKED:
                expectedNextStatus = Order.OrderStatus.SHIPPED;
                break;

            case SHIPPED:
                expectedNextStatus = Order.OrderStatus.OUT_FOR_DELIVERY;
                break;

            case OUT_FOR_DELIVERY:
                expectedNextStatus = Order.OrderStatus.DELIVERED;
                break;

            default:
                throw new IllegalArgumentException(
                        "This order cannot be moved to another status.");
        }

        if (newStatus != expectedNextStatus) {
            throw new IllegalArgumentException(
                    "Invalid status progression. Order is currently " +
                            currentStatus +
                            ". The next status must be " +
                            expectedNextStatus +
                            ".");
        }

        order.setStatus(newStatus);

        Order saved = orderRepo.save(order);

        notificationRepo.save(new Notification(
                order.getCustomer(),
                "Order #" + order.getId() +
                        " Status: " + newStatus.name(),
                "Your agricultural produce order #" +
                        order.getId() +
                        " is now " +
                        newStatus.name()
        ));

        return getOrderById(saved.getId(), null);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, String newStatusStr) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));

        Order.OrderStatus newStatus =
                Order.OrderStatus.valueOf(
                        newStatusStr.toUpperCase().trim());

        Order.OrderStatus oldStatus = order.getStatus();

        order.setStatus(newStatus);

        if (newStatus == Order.OrderStatus.CANCELLED &&
                oldStatus != Order.OrderStatus.CANCELLED) {

            List<OrderItem> items =
                    orderItemRepo.findByOrderId(order.getId());

            for (OrderItem item : items) {
                Product p = item.getProduct();

                p.setQuantity(
                        p.getQuantity() + item.getQuantity());

                p.setAvailable(true);

                productRepo.save(p);
            }
        }

        Order saved = orderRepo.save(order);

        notificationRepo.save(new Notification(
                order.getCustomer(),
                "Order #" + order.getId() +
                        " Status: " + newStatus.name(),
                "Your agricultural produce order #" +
                        order.getId() +
                        " is now " +
                        newStatus.name()
        ));

        return getOrderById(saved.getId(), null);
    }

    @Transactional
    public OrderDto cancelOrder(Long orderId, Long customerId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId));

        if (customerId != null &&
                !order.getCustomer().getId().equals(customerId)) {

            throw new SecurityException(
                    "Unauthorized: You can only cancel your own order.");
        }

        if (order.getStatus() == Order.OrderStatus.SHIPPED ||
                order.getStatus() == Order.OrderStatus.OUT_FOR_DELIVERY ||
                order.getStatus() == Order.OrderStatus.DELIVERED) {

            throw new IllegalStateException(
                    "Order #" + orderId +
                            " cannot be cancelled as it is already " +
                            order.getStatus().name());
        }

        return updateOrderStatus(orderId, "CANCELLED");
    }
}

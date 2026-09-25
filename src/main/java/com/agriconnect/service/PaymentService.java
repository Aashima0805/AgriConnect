package com.agriconnect.service;

import com.agriconnect.dto.PaymentDto;
import com.agriconnect.dto.PaymentRequest;
import com.agriconnect.entity.Order;
import com.agriconnect.entity.Payment;
import com.agriconnect.repository.OrderRepository;
import com.agriconnect.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;

    public PaymentService(PaymentRepository paymentRepo, OrderRepository orderRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
    }

    @Transactional
    public PaymentDto processMockPayment(PaymentRequest req) {
        if (req == null || req.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required for payment processing.");
        }
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        Order order = orderRepo.findById(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + req.getOrderId()));

        if (req.getCustomerId() != null && !order.getCustomer().getId().equals(req.getCustomerId())) {
            throw new SecurityException("Unauthorized: Cannot process payment for another customer's order.");
        }

        Payment.PaymentMethod method;
        try {
            String methodStr = req.getMethod() != null ? req.getMethod().toUpperCase().trim() : "UPI";
            method = Payment.PaymentMethod.valueOf(methodStr);
        } catch (Exception e) {
            method = Payment.PaymentMethod.UPI;
        }

        boolean isSuccessful = req.getSimulateSuccess() == null || req.getSimulateSuccess();

        Payment.PaymentStatus status = isSuccessful ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED;
        String txnRef = (isSuccessful ? "MOCK-AGRI-" : "MOCK-FAIL-") + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Payment payment = new Payment(order, req.getAmount(), method, status, txnRef, true);
        Payment savedPayment = paymentRepo.save(payment);

        // Update Order payment status
        if (isSuccessful) {
            order.setPaymentStatus("PAID");
            if (order.getStatus() == Order.OrderStatus.PLACED) {
                order.setStatus(Order.OrderStatus.CONFIRMED);
            }
        } else {
            order.setPaymentStatus("FAILED");
        }
        orderRepo.save(order);

        return PaymentDto.fromEntity(savedPayment);
    }

    public List<PaymentDto> getPaymentsByOrder(Long orderId, Long requestingCustomerId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required.");
        }
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        if (requestingCustomerId != null && !order.getCustomer().getId().equals(requestingCustomerId)) {
            throw new SecurityException("Unauthorized: You can only view payment records for your own orders.");
        }

        return paymentRepo.findByOrderId(orderId).stream()
                .map(PaymentDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getPaymentsByCustomer(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        return paymentRepo.findByOrderCustomerId(customerId).stream()
                .map(PaymentDto::fromEntity)
                .collect(Collectors.toList());
    }
}

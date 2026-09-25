package com.agriconnect.repository;

import com.agriconnect.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionReference(String transactionReference);
    List<Payment> findByOrderCustomerId(Long customerId);
}

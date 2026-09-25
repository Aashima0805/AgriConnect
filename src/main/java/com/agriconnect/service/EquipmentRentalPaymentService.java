package com.agriconnect.service;

import com.agriconnect.dto.EquipmentRentalPaymentRequest;
import com.agriconnect.entity.EquipmentRental;
import com.agriconnect.repository.EquipmentRentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class EquipmentRentalPaymentService {

    private final EquipmentRentalRepository rentalRepo;

    public EquipmentRentalPaymentService(EquipmentRentalRepository rentalRepo) {
        this.rentalRepo = rentalRepo;
    }

    @Transactional
    public EquipmentRental processPayment(EquipmentRentalPaymentRequest req) {

        if (req == null || req.getRentalId() == null) {
            throw new IllegalArgumentException("Rental ID is required.");
        }

        if (req.getRenterId() == null) {
            throw new IllegalArgumentException("Renter ID is required.");
        }

        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        EquipmentRental rental = rentalRepo.findById(req.getRentalId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Rental not found with ID: " + req.getRentalId()
                        )
                );

        if (!rental.getRenter().getId().equals(req.getRenterId())) {
            throw new SecurityException(
                    "Unauthorized: Cannot process payment for another farmer's rental."
            );
        }

        if (rental.getStatus() != EquipmentRental.RentalStatus.APPROVED) {
            throw new IllegalStateException(
                    "Payment is allowed only for approved rentals."
            );
        }

        if (rental.getPaymentStatus() == EquipmentRental.PaymentStatus.PAID) {
            throw new IllegalStateException(
                    "This rental has already been paid."
            );
        }

        if (req.getAmount().compareTo(rental.getRentalAmount()) != 0) {
            throw new IllegalArgumentException(
                    "Payment amount does not match the rental amount."
            );
        }

        boolean isSuccessful =
                req.getSimulateSuccess() == null || req.getSimulateSuccess();

        if (!isSuccessful) {
            throw new IllegalStateException(
                    "Mock payment failed."
            );
        }

        rental.setPaymentStatus(
                EquipmentRental.PaymentStatus.PAID
        );

        return rentalRepo.save(rental);
    }
}
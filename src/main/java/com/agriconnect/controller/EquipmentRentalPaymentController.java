package com.agriconnect.controller;

import com.agriconnect.dto.EquipmentRentalPaymentRequest;
import com.agriconnect.entity.EquipmentRental;
import com.agriconnect.service.EquipmentRentalPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment-rental-payments")
public class EquipmentRentalPaymentController {

    private final EquipmentRentalPaymentService paymentService;

    public EquipmentRentalPaymentController(
            EquipmentRentalPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> processPayment(
            @RequestBody EquipmentRentalPaymentRequest request) {

        try {

            EquipmentRental rental =
                    paymentService.processPayment(request);

            return ResponseEntity.ok(rental);

        } catch (SecurityException e) {

            return ResponseEntity.status(403)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (IllegalArgumentException | IllegalStateException e) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Unable to process rental payment."));
        }
    }

    public static class ErrorResponse {

        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
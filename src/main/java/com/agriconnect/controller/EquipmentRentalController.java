package com.agriconnect.controller;

import com.agriconnect.dto.EquipmentRentalRequest;
import com.agriconnect.entity.EquipmentRental;
import com.agriconnect.service.EquipmentRentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-rentals")
public class EquipmentRentalController {

    private final EquipmentRentalService rentalService;

    public EquipmentRentalController(EquipmentRentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<?> createRental(
            @RequestBody EquipmentRentalRequest request) {

        try {
            EquipmentRental rental = rentalService.createRental(
                    request.getEquipmentId(),
                    request.getRenterId(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            return ResponseEntity.ok(rental);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<EquipmentRental>> getRenterRentals(
            @PathVariable Long renterId) {

        return ResponseEntity.ok(
                rentalService.getRenterRentals(renterId)
        );
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<EquipmentRental>> getOwnerRentals(
            @PathVariable Long ownerId) {

        return ResponseEntity.ok(
                rentalService.getOwnerRentals(ownerId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRental(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(
                    rentalService.getRental(id)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}/approve")
public ResponseEntity<?> approveRental(
        @PathVariable Long id,
        @RequestParam Long ownerId) {

    try {
        return ResponseEntity.ok(
                rentalService.approveRental(id, ownerId)
        );
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
    }
}

@PutMapping("/{id}/reject")
public ResponseEntity<?> rejectRental(
        @PathVariable Long id,
        @RequestParam Long ownerId) {

    try {
        return ResponseEntity.ok(
                rentalService.rejectRental(id, ownerId)
        );
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", e.getMessage()));
    }
}
}
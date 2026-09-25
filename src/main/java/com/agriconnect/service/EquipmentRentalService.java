package com.agriconnect.service;

import com.agriconnect.entity.Equipment;
import com.agriconnect.entity.EquipmentRental;
import com.agriconnect.entity.User;
import com.agriconnect.repository.EquipmentRentalRepository;
import com.agriconnect.repository.EquipmentRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EquipmentRentalService {

    private final EquipmentRentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public EquipmentRentalService(
            EquipmentRentalRepository rentalRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EquipmentRental createRental(
            Long equipmentId,
            Long renterId,
            LocalDate startDate,
            LocalDate endDate) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        User renter = userRepository.findById(renterId)
                .orElseThrow(() -> new RuntimeException("Renter not found."));
            
        if (renter.getRole() != User.Role.FARMER) {
    throw new RuntimeException("Only farmers can rent agricultural equipment.");
}
        User owner = equipment.getOwner();

        if (owner.getId().equals(renterId)) {
            throw new RuntimeException("You cannot rent your own equipment.");
        }

        if (!equipment.isAvailable()) {
            throw new RuntimeException("This equipment is currently unavailable.");
        }

        if (startDate == null || endDate == null) {
            throw new RuntimeException("Start date and end date are required.");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past.");
        }

        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("End date cannot be before start date.");
        }

        List<EquipmentRental> overlappingRentals =
                rentalRepository.findByEquipmentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        equipmentId,
                        endDate,
                        startDate
                );

        boolean alreadyBooked = overlappingRentals.stream()
                .anyMatch(rental ->
                        rental.getStatus() == EquipmentRental.RentalStatus.PENDING ||
                        rental.getStatus() == EquipmentRental.RentalStatus.APPROVED ||
                        rental.getStatus() == EquipmentRental.RentalStatus.ACTIVE
                );

        if (alreadyBooked) {
            throw new RuntimeException(
                    "Equipment is already booked for the selected dates."
            );
        }

        int numberOfDays =
                (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        BigDecimal rentalAmount =
                equipment.getRentalPricePerDay()
                        .multiply(BigDecimal.valueOf(numberOfDays));

        EquipmentRental rental = new EquipmentRental();

        rental.setEquipment(equipment);
        rental.setRenter(renter);
        rental.setOwner(owner);
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setNumberOfDays(numberOfDays);
        rental.setRentalAmount(rentalAmount);
        rental.setStatus(EquipmentRental.RentalStatus.PENDING);
        rental.setPaymentStatus(EquipmentRental.PaymentStatus.PENDING);

        return rentalRepository.save(rental);
    }

    public List<EquipmentRental> getRenterRentals(Long renterId) {
        return rentalRepository.findByRenterIdOrderByCreatedAtDesc(renterId);
    }

    public List<EquipmentRental> getOwnerRentals(Long ownerId) {
        return rentalRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    public EquipmentRental getRental(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found."));
    }

    @Transactional
public EquipmentRental approveRental(Long rentalId, Long ownerId) {

    EquipmentRental rental = getRental(rentalId);

    if (!rental.getOwner().getId().equals(ownerId)) {
        throw new RuntimeException("You are not the owner of this equipment.");
    }

    if (rental.getStatus() != EquipmentRental.RentalStatus.PENDING) {
        throw new RuntimeException("Only pending rentals can be approved.");
    }

    List<EquipmentRental> overlappingRentals =
            rentalRepository.findByEquipmentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    rental.getEquipment().getId(),
                    rental.getEndDate(),
                    rental.getStartDate()
            );

    boolean conflict = overlappingRentals.stream()
            .anyMatch(other ->
                    !other.getId().equals(rentalId) &&
                    (other.getStatus() == EquipmentRental.RentalStatus.APPROVED ||
                     other.getStatus() == EquipmentRental.RentalStatus.ACTIVE)
            );

    if (conflict) {
        throw new RuntimeException(
                "Another rental already exists for these dates."
        );
    }

    rental.setStatus(EquipmentRental.RentalStatus.APPROVED);

    return rentalRepository.save(rental);
}

@Transactional
public EquipmentRental rejectRental(Long rentalId, Long ownerId) {

    EquipmentRental rental = getRental(rentalId);

    if (!rental.getOwner().getId().equals(ownerId)) {
        throw new RuntimeException("You are not the owner of this equipment.");
    }

    if (rental.getStatus() != EquipmentRental.RentalStatus.PENDING) {
        throw new RuntimeException("Only pending rentals can be rejected.");
    }

    rental.setStatus(EquipmentRental.RentalStatus.REJECTED);

    return rentalRepository.save(rental);
}

}
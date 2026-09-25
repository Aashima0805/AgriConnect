package com.agriconnect.repository;

import com.agriconnect.entity.EquipmentRental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EquipmentRentalRepository extends JpaRepository<EquipmentRental, Long> {

    List<EquipmentRental> findByRenterIdOrderByCreatedAtDesc(Long renterId);

    List<EquipmentRental> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<EquipmentRental> findByEquipmentIdOrderByCreatedAtDesc(Long equipmentId);

    List<EquipmentRental> findByEquipmentIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long equipmentId,
            LocalDate endDate,
            LocalDate startDate
    );
}
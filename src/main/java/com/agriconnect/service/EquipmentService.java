package com.agriconnect.service;

import com.agriconnect.dto.EquipmentDto;
import com.agriconnect.entity.Equipment;
import com.agriconnect.entity.User;
import com.agriconnect.repository.EquipmentRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepo;
    private final UserRepository userRepo;

    public EquipmentService(EquipmentRepository equipmentRepo, UserRepository userRepo) {
        this.equipmentRepo = equipmentRepo;
        this.userRepo = userRepo;
    }

    public List<EquipmentDto> listAvailableEquipment() {
        return equipmentRepo.findByAvailableTrue().stream()
                .map(EquipmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<EquipmentDto> getEquipmentById(Long id) {
        return equipmentRepo.findById(id).map(EquipmentDto::fromEntity);
    }

    public List<EquipmentDto> getEquipmentByOwner(Long ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required.");
        }
        return equipmentRepo.findByOwnerId(ownerId).stream()
                .map(EquipmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipmentDto addEquipment(Long ownerId, EquipmentDto dto) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Equipment name is required.");
        }
        if (dto.getRentalPricePerDay() == null || dto.getRentalPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rental price per day must be greater than 0.");
        }

        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner/Farmer not found with id: " + ownerId));

        Equipment equipment = new Equipment();
        equipment.setOwner(owner);
        equipment.setName(dto.getName().trim());
        equipment.setType(dto.getType() != null ? dto.getType().trim() : "Other");
        equipment.setDescription(dto.getDescription());
        equipment.setRentalPricePerDay(dto.getRentalPricePerDay());
        equipment.setLocation(dto.getLocation());
        equipment.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);

        Equipment saved = equipmentRepo.save(equipment);
        return EquipmentDto.fromEntity(saved);
    }

    @Transactional
    public EquipmentDto updateEquipment(Long equipmentId, Long ownerId, EquipmentDto dto) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with id: " + equipmentId));

        if (ownerId != null && !equipment.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: you can only update your own equipment.");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            equipment.setName(dto.getName().trim());
        }
        if (dto.getType() != null) {
            equipment.setType(dto.getType().trim());
        }
        if (dto.getDescription() != null) {
            equipment.setDescription(dto.getDescription());
        }
        if (dto.getRentalPricePerDay() != null) {
            if (dto.getRentalPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Rental price per day must be greater than 0.");
            }
            equipment.setRentalPricePerDay(dto.getRentalPricePerDay());
        }
        if (dto.getLocation() != null) {
            equipment.setLocation(dto.getLocation());
        }
        if (dto.getAvailable() != null) {
            equipment.setAvailable(dto.getAvailable());
        }

        Equipment updated = equipmentRepo.save(equipment);
        return EquipmentDto.fromEntity(updated);
    }

    @Transactional
    public void deleteEquipment(Long equipmentId, Long ownerId) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with id: " + equipmentId));

        if (ownerId != null && !equipment.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: you can only delete your own equipment.");
        }

        equipmentRepo.delete(equipment);
    }
}

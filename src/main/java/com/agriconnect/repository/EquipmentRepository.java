package com.agriconnect.repository;

import com.agriconnect.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByOwnerId(Long ownerId);
    List<Equipment> findByAvailableTrue();
    long countByOwnerId(Long ownerId);
}

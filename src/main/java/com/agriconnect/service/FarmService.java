package com.agriconnect.service;

import com.agriconnect.dto.FarmDetailsDto;
import com.agriconnect.entity.FarmDetails;
import com.agriconnect.entity.User;
import com.agriconnect.repository.FarmDetailsRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FarmService {

    private final FarmDetailsRepository farmRepo;
    private final UserRepository userRepo;

    public FarmService(FarmDetailsRepository farmRepo, UserRepository userRepo) {
        this.farmRepo = farmRepo;
        this.userRepo = userRepo;
    }

    public Optional<FarmDetailsDto> getFarmDetails(Long farmerId) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        return farmRepo.findByFarmerId(farmerId).map(FarmDetailsDto::fromEntity);
    }

    @Transactional
    public FarmDetailsDto saveOrUpdateFarm(Long farmerId, FarmDetailsDto dto) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        if (dto.getLandArea() == null || dto.getLandArea() <= 0) {
            throw new IllegalArgumentException("Land area must be a positive number.");
        }

        User farmer = userRepo.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found with id: " + farmerId));

        FarmDetails farm = farmRepo.findByFarmerId(farmerId).orElseGet(() -> {
            FarmDetails newFarm = new FarmDetails();
            newFarm.setFarmer(farmer);
            return newFarm;
        });

        // Ownership verification: ensure existing farm record belongs to this farmer
        if (farm.getFarmer() != null && !farm.getFarmer().getId().equals(farmerId)) {
            throw new IllegalStateException("Ownership mismatch: cannot update another farmer's farm details.");
        }

        farm.setFarmer(farmer);
        farm.setLandArea(dto.getLandArea());
        farm.setLandUnit(dto.getLandUnit() != null && !dto.getLandUnit().isBlank() ? dto.getLandUnit() : "acres");
        farm.setLocation(dto.getLocation());
        farm.setCrops(dto.getCrops());
        farm.setSoilType(dto.getSoilType());
        farm.setIrrigationType(dto.getIrrigationType());

        FarmDetails saved = farmRepo.save(farm);
        return FarmDetailsDto.fromEntity(saved);
    }
}

package com.agriconnect.service;

import com.agriconnect.dto.GrievanceDto;
import com.agriconnect.entity.Grievance;
import com.agriconnect.entity.User;
import com.agriconnect.repository.GrievanceRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GrievanceService {

    private final GrievanceRepository grievanceRepo;
    private final UserRepository userRepo;

    public GrievanceService(GrievanceRepository grievanceRepo, UserRepository userRepo) {
        this.grievanceRepo = grievanceRepo;
        this.userRepo = userRepo;
    }

    public List<GrievanceDto> listGrievances(String status) {
        if (status != null && (status.equalsIgnoreCase("ELIGIBLE") || status.equalsIgnoreCase("OPEN_AND_PARTIAL"))) {
            return getEligibleGrievances();
        }
        List<Grievance> list;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            list = grievanceRepo.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase());
        } else {
            list = grievanceRepo.findAllByOrderByCreatedAtDesc();
        }
        return list.stream().map(GrievanceDto::fromEntity).collect(Collectors.toList());
    }

    public List<GrievanceDto> getEligibleGrievances() {
        return grievanceRepo.findByStatusInOrderByCreatedAtDesc(List.of("OPEN", "PARTIALLY_FUNDED"))
                .stream()
                .filter(g -> {
                    BigDecimal target = g.getTargetAmount() != null ? g.getTargetAmount() : BigDecimal.ZERO;
                    BigDecimal rec = g.getReceivedAmount() != null ? g.getReceivedAmount() : BigDecimal.ZERO;
                    return rec.compareTo(target) < 0;
                })
                .map(GrievanceDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<GrievanceDto> getGrievanceById(Long id) {
        return grievanceRepo.findById(id).map(GrievanceDto::fromEntity);
    }

    public List<GrievanceDto> getGrievancesByFarmer(Long farmerId) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        return grievanceRepo.findByFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(GrievanceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public GrievanceDto createGrievance(Long farmerId, GrievanceDto dto) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Grievance title is required.");
        }
        if (dto.getTargetAmount() == null || dto.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than 0.");
        }

        User farmer = userRepo.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found with id: " + farmerId));

        Grievance grievance = new Grievance();
        grievance.setFarmer(farmer);
        grievance.setTitle(dto.getTitle().trim());
        grievance.setDescription(dto.getDescription());
        grievance.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : "General");
        grievance.setTargetAmount(dto.getTargetAmount());
        grievance.setReceivedAmount(BigDecimal.ZERO);
        grievance.setStatus("OPEN");
        grievance.setLocation(dto.getLocation());

        Grievance saved = grievanceRepo.save(grievance);
        return GrievanceDto.fromEntity(saved);
    }

    @Transactional
    public GrievanceDto updateGrievance(Long grievanceId, Long farmerId, GrievanceDto dto) {
        Grievance grievance = grievanceRepo.findById(grievanceId)
                .orElseThrow(() -> new IllegalArgumentException("Grievance not found with id: " + grievanceId));

        if (farmerId != null && !grievance.getFarmer().getId().equals(farmerId)) {
            throw new SecurityException("Unauthorized: you can only update your own grievances.");
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            grievance.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            grievance.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            grievance.setCategory(dto.getCategory().trim());
        }
        if (dto.getTargetAmount() != null) {
            if (dto.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Target amount must be greater than 0.");
            }
            grievance.setTargetAmount(dto.getTargetAmount());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            grievance.setStatus(dto.getStatus().trim().toUpperCase());
        }
        if (dto.getLocation() != null) {
            grievance.setLocation(dto.getLocation());
        }

        Grievance updated = grievanceRepo.save(grievance);
        return GrievanceDto.fromEntity(updated);
    }

    @Transactional
    public GrievanceDto updateStatus(Long grievanceId, Long farmerId, String status) {
        Grievance grievance = grievanceRepo.findById(grievanceId)
                .orElseThrow(() -> new IllegalArgumentException("Grievance not found with id: " + grievanceId));

        if (farmerId != null && !grievance.getFarmer().getId().equals(farmerId)) {
            throw new SecurityException("Unauthorized: you can only modify status for your own grievances.");
        }

        if (status != null && !status.isBlank()) {
            grievance.setStatus(status.trim().toUpperCase());
        }

        Grievance updated = grievanceRepo.save(grievance);
        return GrievanceDto.fromEntity(updated);
    }
}

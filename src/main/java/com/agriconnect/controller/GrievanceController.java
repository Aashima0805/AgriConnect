package com.agriconnect.controller;

import com.agriconnect.dto.GrievanceDto;
import com.agriconnect.service.GrievanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grievances")
@CrossOrigin(origins = "*")
public class GrievanceController {

    private final GrievanceService grievanceService;

    public GrievanceController(GrievanceService grievanceService) {
        this.grievanceService = grievanceService;
    }

    @GetMapping
    public List<GrievanceDto> listGrievances(@RequestParam(required = false) String status) {
        return grievanceService.listGrievances(status);
    }

    @GetMapping("/eligible")
    public List<GrievanceDto> listEligibleGrievances() {
        return grievanceService.getEligibleGrievances();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrievanceDto> getGrievance(@PathVariable Long id) {
        return grievanceService.getGrievanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/farmer/{farmerId}")
    public List<GrievanceDto> getFarmerGrievances(@PathVariable Long farmerId) {
        return grievanceService.getGrievancesByFarmer(farmerId);
    }

    @PostMapping
    public ResponseEntity<?> createGrievance(@Valid @RequestBody GrievanceDto dto) {
        try {
            GrievanceDto created = grievanceService.createGrievance(dto.getFarmerId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrievance(@PathVariable Long id, @RequestBody GrievanceDto dto) {
        try {
            GrievanceDto updated = grievanceService.updateGrievance(id, dto.getFarmerId(), dto);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam(required = false) Long farmerId,
                                          @RequestBody Map<String, String> req) {
        try {
            String status = req.get("status");
            GrievanceDto updated = grievanceService.updateStatus(id, farmerId, status);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

package com.agriconnect.controller;

import com.agriconnect.dto.DonationDto;
import com.agriconnect.dto.DonationRequest;
import com.agriconnect.dto.GrievanceDto;
import com.agriconnect.entity.User;
import com.agriconnect.repository.UserRepository;
import com.agriconnect.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;
    private final UserRepository userRepo;

    public DonationController(DonationService donationService, UserRepository userRepo) {
        this.donationService = donationService;
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<DonationDto> getAllDonations() {
        return donationService.getAllDonations();
    }

    @GetMapping({"/eligible-grievances", "/grievances/eligible", "/grievances"})
    public List<GrievanceDto> getEligibleGrievances() {
        return donationService.getEligibleGrievances();
    }

    @GetMapping("/donor/{id}")
    public ResponseEntity<?> getDonorHistory(@PathVariable Long id,
                                             @RequestParam(required = false) Long requestingUserId,
                                             @RequestParam(required = false) Long requestingDonorId,
                                             @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                             Principal principal) {
        try {
            Long authUserId = resolveRequestingUserId(requestingUserId, requestingDonorId, headerUserId, principal);
            List<DonationDto> history = donationService.getDonorHistory(id, authUserId);
            return ResponseEntity.ok(history);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/grievance/{id}")
    public List<DonationDto> getGrievanceDonations(@PathVariable Long id) {
        return donationService.getGrievanceDonations(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationDetails(@PathVariable Long id,
                                                @RequestParam(required = false) Long requestingUserId,
                                                @RequestParam(required = false) Long requestingDonorId,
                                                @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                                Principal principal) {
        try {
            Long authUserId = resolveRequestingUserId(requestingUserId, requestingDonorId, headerUserId, principal);
            DonationDto dto = donationService.getDonationById(id, authUserId);
            return ResponseEntity.ok(dto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<?> getDonationByReceipt(@PathVariable String receiptNumber,
                                                  @RequestParam(required = false) Long requestingUserId,
                                                  @RequestParam(required = false) Long requestingDonorId,
                                                  @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                                  Principal principal) {
        try {
            Long authUserId = resolveRequestingUserId(requestingUserId, requestingDonorId, headerUserId, principal);
            DonationDto dto = donationService.getDonationByReceipt(receiptNumber, authUserId);
            return ResponseEntity.ok(dto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> makeDonation(@Valid @RequestBody DonationRequest req,
                                          @RequestParam(required = false) Long requestingUserId,
                                          @RequestParam(required = false) Long requestingDonorId,
                                          @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                          Principal principal) {
        try {
            Long authUserId = resolveRequestingUserId(requestingUserId, requestingDonorId, headerUserId, principal);
            DonationDto created = donationService.processDonation(req, authUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to process donation: " + e.getMessage()));
        }
    }

    private Long resolveRequestingUserId(Long requestingUserId, Long requestingDonorId, Long headerUserId, Principal principal) {
        if (requestingUserId != null) return requestingUserId;
        if (requestingDonorId != null) return requestingDonorId;
        if (headerUserId != null) return headerUserId;
        if (principal != null) {
            String username = principal.getName();
            if (username != null) {
                try {
                    return Long.valueOf(username);
                } catch (NumberFormatException ignored) {}
                return userRepo.findByEmail(username.trim().toLowerCase())
                        .map(User::getId)
                        .orElse(null);
            }
        }
        return null;
    }
}

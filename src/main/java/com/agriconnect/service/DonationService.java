package com.agriconnect.service;

import com.agriconnect.dto.DonationDto;
import com.agriconnect.dto.DonationRequest;
import com.agriconnect.dto.GrievanceDto;
import com.agriconnect.entity.Donation;
import com.agriconnect.entity.Grievance;
import com.agriconnect.entity.Notification;
import com.agriconnect.entity.User;
import com.agriconnect.repository.DonationRepository;
import com.agriconnect.repository.GrievanceRepository;
import com.agriconnect.repository.NotificationRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationRepository donationRepo;
    private final GrievanceRepository grievanceRepo;
    private final UserRepository userRepo;
    private final NotificationRepository notificationRepo;

    public DonationService(DonationRepository donationRepo,
                           GrievanceRepository grievanceRepo,
                           UserRepository userRepo,
                           NotificationRepository notificationRepo) {
        this.donationRepo = donationRepo;
        this.grievanceRepo = grievanceRepo;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
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

    @Transactional
    public DonationDto processDonation(DonationRequest request, Long requestingUserId) {
        if (request == null) {
            throw new IllegalArgumentException("Donation request body cannot be empty.");
        }
        if (request.getDonorId() == null) {
            throw new IllegalArgumentException("Donor ID is required.");
        }
        if (request.getGrievanceId() == null) {
            throw new IllegalArgumentException("Grievance ID is required.");
        }
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Donation amount is required.");
        }
        if (request.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Donation amount must be at least ₹1.00.");
        }

        // Authorization check: requesting user must match donor if provided
        if (requestingUserId != null && !requestingUserId.equals(request.getDonorId())) {
            throw new SecurityException("Unauthorized: You cannot make a donation on behalf of another user.");
        }

        User donor = userRepo.findById(request.getDonorId())
                .orElseThrow(() -> new IllegalArgumentException("Donor not found with ID: " + request.getDonorId()));

        Grievance grievance = grievanceRepo.findById(request.getGrievanceId())
                .orElseThrow(() -> new IllegalArgumentException("Grievance not found with ID: " + request.getGrievanceId()));

        String currentStatus = grievance.getStatus() != null ? grievance.getStatus().toUpperCase() : "OPEN";
        if ("FUNDED".equals(currentStatus) || "CLOSED".equals(currentStatus)) {
            throw new IllegalArgumentException("This grievance is already " + currentStatus.toLowerCase() + " and not eligible for further donations.");
        }

        BigDecimal targetAmount = grievance.getTargetAmount() != null ? grievance.getTargetAmount() : BigDecimal.ZERO;
        BigDecimal currentReceived = grievance.getReceivedAmount() != null ? grievance.getReceivedAmount() : BigDecimal.ZERO;
        BigDecimal remainingAmount = targetAmount.subtract(currentReceived);

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The target amount for this grievance has already been reached.");
        }

        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException("Donation amount (₹" + request.getAmount() + ") exceeds the remaining target amount of ₹" + remainingAmount + ".");
        }

        // 1. Generate Mock Receipt and Transaction Reference
        String receiptNumber = "REC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String txnReference = "TXN-MOCK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

        // 2. Persist Donation
        Donation donation = new Donation(donor, grievance, request.getAmount(), "SUCCESS", receiptNumber, txnReference);
        Donation savedDonation = donationRepo.save(donation);

        // 3. Consistently update Grievance funding information and status
        BigDecimal updatedTotal = currentReceived.add(request.getAmount());
        grievance.setReceivedAmount(updatedTotal);

        if (updatedTotal.compareTo(targetAmount) >= 0) {
            grievance.setStatus("FUNDED");
        } else {
            grievance.setStatus("PARTIALLY_FUNDED");
        }
        grievanceRepo.save(grievance);

        // 4. Create Audit Notifications for Farmer and Donor
        if (grievance.getFarmer() != null) {
            notificationRepo.save(new Notification(
                    grievance.getFarmer(),
                    "Donation Received!",
                    "A generous contribution of ₹" + request.getAmount() + " was donated towards your grievance '"
                            + grievance.getTitle() + "' by " + donor.getName() + ". Receipt: " + receiptNumber
            ));
        }

        notificationRepo.save(new Notification(
                donor,
                "Donation Successful",
                "Your donation of ₹" + request.getAmount() + " to '" + grievance.getTitle()
                        + "' was successful. Receipt #" + receiptNumber
        ));

        return DonationDto.fromEntity(savedDonation);
    }

    public List<DonationDto> getDonorHistory(Long donorId, Long requestingUserId) {
        if (donorId == null) {
            throw new IllegalArgumentException("Donor ID is required.");
        }
        if (requestingUserId != null && !donorId.equals(requestingUserId)) {
            throw new SecurityException("Unauthorized: You cannot access another donor's donation history.");
        }
        if (!userRepo.existsById(donorId)) {
            throw new IllegalArgumentException("Donor not found with ID: " + donorId);
        }
        return donationRepo.findByDonorIdOrderByDonatedAtDesc(donorId).stream()
                .map(DonationDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DonationDto getDonationById(Long donationId, Long requestingUserId) {
        if (donationId == null) {
            throw new IllegalArgumentException("Donation ID is required.");
        }
        Donation donation = donationRepo.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation record not found with ID: " + donationId));

        if (requestingUserId != null && !donation.getDonor().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized: You can only view details of your own donations.");
        }

        return DonationDto.fromEntity(donation);
    }

    public DonationDto getDonationByReceipt(String receiptNumber, Long requestingUserId) {
        if (receiptNumber == null || receiptNumber.isBlank()) {
            throw new IllegalArgumentException("Receipt number is required.");
        }
        Donation donation = donationRepo.findByReceiptNumber(receiptNumber.trim())
                .orElseThrow(() -> new IllegalArgumentException("Donation record not found with receipt number: " + receiptNumber.trim()));

        if (requestingUserId != null && !donation.getDonor().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized: You can only view details of your own donations.");
        }

        return DonationDto.fromEntity(donation);
    }

    public List<DonationDto> getGrievanceDonations(Long grievanceId) {
        if (grievanceId == null) {
            throw new IllegalArgumentException("Grievance ID is required.");
        }
        return donationRepo.findByGrievanceIdOrderByDonatedAtDesc(grievanceId).stream()
                .map(DonationDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DonationDto> getAllDonations() {
        return donationRepo.findAllByOrderByDonatedAtDesc().stream()
                .map(DonationDto::fromEntity)
                .collect(Collectors.toList());
    }
}

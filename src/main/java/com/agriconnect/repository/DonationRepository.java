package com.agriconnect.repository;

import com.agriconnect.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorIdOrderByDonatedAtDesc(Long donorId);
    List<Donation> findByGrievanceIdOrderByDonatedAtDesc(Long grievanceId);
    List<Donation> findAllByOrderByDonatedAtDesc();
    Optional<Donation> findByIdAndDonorId(Long id, Long donorId);
    Optional<Donation> findByReceiptNumber(String receiptNumber);
    Optional<Donation> findByTransactionReference(String transactionReference);
    
    @Query("SELECT d FROM Donation d WHERE d.grievance.farmer.id = :farmerId ORDER BY d.donatedAt DESC")
    List<Donation> findByFarmerIdOrderByDonatedAtDesc(@Param("farmerId") Long farmerId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.grievance.farmer.id = :farmerId AND d.paymentStatus = 'SUCCESS'")
    BigDecimal sumDonationsForFarmer(@Param("farmerId") Long farmerId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.paymentStatus = 'SUCCESS'")
    BigDecimal sumTotalDonations();
}

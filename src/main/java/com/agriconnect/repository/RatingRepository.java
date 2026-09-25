package com.agriconnect.repository;

import com.agriconnect.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
    Optional<Rating> findByCustomerIdAndFarmerIdAndOrderId(Long customerId, Long farmerId, Long orderId);
    
    @Query("SELECT AVG((r.productQuality + r.deliveryExperience) / 2.0) FROM Rating r WHERE r.farmer.id = :farmerId")
    Double findAverageRatingByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.farmer.id = :farmerId")
    long countRatingsByFarmerId(@Param("farmerId") Long farmerId);
}

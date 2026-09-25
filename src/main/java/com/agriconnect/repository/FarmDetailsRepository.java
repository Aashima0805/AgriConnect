package com.agriconnect.repository;

import com.agriconnect.entity.FarmDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FarmDetailsRepository extends JpaRepository<FarmDetails, Long> {
    Optional<FarmDetails> findByFarmerId(Long farmerId);
}

package com.agriconnect.repository;

import com.agriconnect.entity.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);
    List<Grievance> findByStatusOrderByCreatedAtDesc(String status);
    List<Grievance> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    List<Grievance> findAllByOrderByCreatedAtDesc();
    long countByFarmerId(Long farmerId);
}

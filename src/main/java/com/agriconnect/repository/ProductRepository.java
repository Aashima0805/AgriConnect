package com.agriconnect.repository;

import com.agriconnect.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFarmerId(Long farmerId);
    List<Product> findByAvailableTrue();
    List<Product> findByCategoryIgnoreCaseAndAvailableTrue(String category);
    List<Product> findByNameContainingIgnoreCaseAndAvailableTrue(String name);
    long countByFarmerId(Long farmerId);
}

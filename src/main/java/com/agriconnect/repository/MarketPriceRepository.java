package com.agriconnect.repository;

import com.agriconnect.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {
    List<MarketPrice> findByCropNameContainingIgnoreCase(String cropName);
    List<MarketPrice> findByMarketContainingIgnoreCase(String market);
    List<MarketPrice> findByCropNameContainingIgnoreCaseOrMarketContainingIgnoreCaseOrCategoryContainingIgnoreCase(String crop, String market, String category);
    List<MarketPrice> findAllByOrderByDateDesc();
    List<MarketPrice> findByIsDemo(Boolean isDemo);
}

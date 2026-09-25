package com.agriconnect.repository;

import com.agriconnect.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN OrderItem oi ON oi.order = o WHERE oi.product.farmer.id = :farmerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN OrderItem oi ON oi.order = o WHERE oi.product.farmer.id = :farmerId")
    long countOrdersByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN OrderItem oi ON oi.order = o WHERE oi.product.farmer.id = :farmerId AND o.status IN ('PLACED', 'CONFIRMED', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY')")
    long countPendingOrdersByFarmerId(@Param("farmerId") Long farmerId);
}

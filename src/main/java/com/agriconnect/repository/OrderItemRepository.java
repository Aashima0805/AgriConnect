package com.agriconnect.repository;

import com.agriconnect.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.order.customer.id = :customerId AND oi.product.id = :productId AND oi.order.status != com.agriconnect.entity.Order.OrderStatus.CANCELLED")
    boolean hasCustomerPurchasedProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);

    boolean existsByOrderCustomerIdAndProductId(Long customerId, Long productId);
}

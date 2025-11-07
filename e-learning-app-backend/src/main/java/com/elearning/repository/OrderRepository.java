package com.elearning.repository;

import com.elearning.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByIdAndUserId(Integer orderId, Integer userId);
    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.course WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findOrderByIdAndUserIdWithDetails(Integer orderId, Integer userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.course WHERE o.id = :orderId")
    Optional<Order> findOrderByIdWithDetails(Integer orderId);
}

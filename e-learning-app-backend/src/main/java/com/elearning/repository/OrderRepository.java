package com.elearning.repository;

import com.elearning.entity.Order;
import com.elearning.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByIdAndUserId(Integer orderId, Integer userId);

    Optional<Order> findByUserIdAndCourseIdAndStatus(Integer user_id, Integer course_id, OrderStatus status);
}

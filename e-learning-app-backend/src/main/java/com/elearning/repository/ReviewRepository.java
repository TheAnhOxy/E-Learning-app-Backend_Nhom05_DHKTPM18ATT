package com.elearning.repository;

import com.elearning.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findAllByCourseIdOrderByCreatedAtDesc(Integer courseId);

    Optional<Review> findByUserIdAndCourseId(Integer userId, Integer courseId);

    Page<Review> findAllByCourse_IdOrderByCreatedAtDesc(Integer courseId, Pageable pageable);
}
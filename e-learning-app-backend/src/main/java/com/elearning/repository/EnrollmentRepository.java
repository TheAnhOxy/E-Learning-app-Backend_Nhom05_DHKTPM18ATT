package com.elearning.repository;

import com.elearning.entity.Enrollment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByUserIdAndCourseId(Integer userId, @NotNull(message = "ID Khóa học không được để trống") Integer courseId);

    Enrollment findByOrderId(Integer id);

    List<Enrollment> findAllWithCourseByUserId(Integer userId);
}

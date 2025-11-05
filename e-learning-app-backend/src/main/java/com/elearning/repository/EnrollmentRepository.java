package com.elearning.repository;

import com.elearning.entity.Enrollment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByUserIdAndCourseId(Integer userId, @NotNull(message = "ID Khóa học không được để trống") Integer courseId);

    boolean existsByUser_IdAndCourse_Id(Integer userId, Integer courseId);
}

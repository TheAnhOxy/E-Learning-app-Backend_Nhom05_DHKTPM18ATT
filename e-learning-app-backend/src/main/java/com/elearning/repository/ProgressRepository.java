package com.elearning.repository;


import com.elearning.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {
    @Query("SELECT COUNT(p) FROM Progress p " +
            "WHERE p.lesson.section.course.id = :courseId " +
            "  AND p.enrollment.user.id = :userId " +
            "  AND p.isCompleted = true")
    int countCompletedByCourseAndUser(@Param("courseId") Integer courseId, @Param("userId") Integer userId);
}

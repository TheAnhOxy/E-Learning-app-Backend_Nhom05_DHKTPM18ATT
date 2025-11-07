package com.elearning.repository;

import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import com.elearning.modal.dto.response.TimeSeriesDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findAll(Specification<User> spec, Pageable pageable);
//    @Query("SELECT CAST(u.createdAt AS DATE), COUNT(u.id) " +
//            "FROM User u " +
//            "WHERE u.role = 'student' AND u.createdAt BETWEEN :startDate AND :endDate " +
//            "GROUP BY CAST(u.createdAt AS DATE) " +
//            "ORDER BY CAST(u.createdAt AS DATE) ASC")
//    List<Object[]> getNewStudentStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countByRole(UserRole userRole);

    boolean existsByEmail(String mail);
    Optional<User> findByUsernameOrEmail(String username, String email);


    @Query(value = "SELECT CAST(created_at AS DATE) as reg_date, COUNT(id) as student_count " +
            "FROM users " +
            "WHERE role = 'student' AND created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY reg_date " +
            "ORDER BY reg_date ASC", nativeQuery = true)
    List<Object[]> getNewStudentStatsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // --- BỔ SUNG 3 (Đếm học viên cho Instructor) ---
    @Query("SELECT COUNT(DISTINCT e.user.id) " +
            "FROM Enrollment e " +
            "WHERE e.course.instructor.id = :instructorId")
    Long countStudentsByInstructor(@Param("instructorId") Integer instructorId);

    boolean existsByUsername(String username);
}

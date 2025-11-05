package com.elearning.repository;

import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import com.elearning.modal.dto.response.TimeSeriesDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findAll(Specification<User> spec, Pageable pageable);
    @Query("SELECT CAST(u.createdAt AS DATE), COUNT(u.id) " +
            "FROM User u " +
            "WHERE u.role = 'student' AND u.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(u.createdAt AS DATE) " +
            "ORDER BY CAST(u.createdAt AS DATE) ASC")
    List<Object[]> getNewStudentStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countByRole(UserRole userRole);
}

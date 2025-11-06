package com.elearning.repository;

import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    // Tìm danh sách Top 15 Teachers đầu tiên
    List<User> findTop15ByRoleOrderByIdAsc(UserRole role);
    Optional<User> findByIdAndRole(Integer id, UserRole userRole);

    Page<User> findAll(Specification<User> spec, Pageable pageable);
    @Query("SELECT CAST(u.createdAt AS DATE), COUNT(u.id) " +
            "FROM User u " +
            "WHERE u.role = 'student' AND u.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(u.createdAt AS DATE) " +
            "ORDER BY CAST(u.createdAt AS DATE) ASC")
    List<Object[]> getNewStudentStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countByRole(UserRole userRole);

    boolean existsByEmail(String mail);
    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);
}

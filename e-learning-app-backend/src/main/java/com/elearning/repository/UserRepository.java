package com.elearning.repository;

import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Tìm danh sách Top 15 Teachers đầu tiên
    List<User> findTop15ByRoleOrderByIdAsc(UserRole role);

    Optional<User> findByIdAndRole(Integer id, UserRole userRole);
}

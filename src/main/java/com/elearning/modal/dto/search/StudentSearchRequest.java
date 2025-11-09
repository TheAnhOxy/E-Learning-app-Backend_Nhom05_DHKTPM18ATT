package com.elearning.modal.dto.search;

import com.elearning.enums.UserRole;
import lombok.Data;

@Data
public class StudentSearchRequest {
    // Tìm kiếm chung cho username, fullName, email
    private String query;

    // Lọc chính xác
    private UserRole role;

    // private String status;
}
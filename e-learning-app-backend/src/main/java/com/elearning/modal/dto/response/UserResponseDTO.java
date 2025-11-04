package com.elearning.modal.dto.response;
import com.elearning.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
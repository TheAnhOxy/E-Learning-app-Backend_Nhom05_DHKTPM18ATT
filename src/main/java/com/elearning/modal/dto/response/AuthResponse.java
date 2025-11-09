package com.elearning.modal.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthResponse {
    private final Integer id;
    private final String username;
    private final String fullName;
    private final String role;
    private final String avatarUrl;
}
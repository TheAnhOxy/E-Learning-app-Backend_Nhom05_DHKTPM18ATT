package com.elearning.controller.admin;

import com.elearning.modal.dto.request.LoginRequest;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.response.AuthResponse;
import com.elearning.service.CustomUserDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        log.info("Yêu cầu đăng nhập API (Session) cho email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // --- Tạo Session ---
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        // ---------------------

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("Đăng nhập thành công cho User ID: {}, Role: {}", userDetails.getId(), userDetails.getRole());

        // Trả về thông tin user (Không trả về token)
        AuthResponse authResponse = new AuthResponse(
                userDetails.getId(),
                userDetails.getUsername(), // Đây là email
                userDetails.getFullName(),
                userDetails.getRole().name()
        );

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đăng nhập thành công!")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // API này dùng để kiểm tra session
        log.info("User ID {} đang kiểm tra session (/me)", userDetails.getId());
        AuthResponse authResponse = new AuthResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFullName(),
                userDetails.getRole().name()
        );
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin user thành công")
                .data(authResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(HttpServletRequest request) {
        log.info("User yêu cầu đăng xuất (Session)");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Hủy session
        }
        SecurityContextHolder.clearContext(); // Xóa context

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đăng xuất thành công")
                .build();
        return ResponseEntity.ok(response);
    }
}





package com.elearning.controller.admin;


import com.elearning.modal.dto.request.NotificationRequestDTO;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.search.StudentSearchRequest;
import com.elearning.service.CustomUserDetails;
import com.elearning.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public class AdminStudentController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> searchStudents(
            @Valid StudentSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("User ID {} (Role: {}) tìm kiếm user...", userDetails.getId(), userDetails.getRole());
        var studentPage = userService.searchStudents(searchRequest, pageable, userDetails);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách user thành công")
                .data(studentPage)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getStudentDetails(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("User ID {} (Role: {}) lấy chi tiết User ID: {}", userDetails.getId(), userDetails.getRole(), userId);
        var studentDetail = userService.getStudentDetails(userId, userDetails);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết user thành công")
                .data(studentDetail)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateStudent(
            @PathVariable Integer userId,
            @Valid @RequestBody StudentUpdateRequestDTO updateRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("User ID {} (Role: {}) cập nhật User ID: {}", userDetails.getId(), userDetails.getRole(), userId);
        var updatedStudent = userService.updateStudent(userId, updateRequestDTO, userDetails);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật user thành công")
                .data(updatedStudent)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteStudent(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("User ID {} (Role: {}) xóa User ID: {}", userDetails.getId(), userDetails.getRole(), userId);
        userService.deleteStudent(userId, userDetails);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa user thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/notify")
    public ResponseEntity<ApiResponse> notifyStudent(
            @PathVariable Integer userId,
            @Valid @RequestBody NotificationRequestDTO notificationRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("User ID {} (Role: {}) gửi thông báo cho User ID: {}", userDetails.getId(), userDetails.getRole(), userId);
        userService.sendNotificationToStudent(userId, notificationRequestDTO, userDetails);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Gửi thông báo thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
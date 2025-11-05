package com.elearning.controller.admin;


import com.elearning.modal.dto.request.NotificationRequestDTO;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.search.StudentSearchRequest;
import com.elearning.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
@Slf4j
public class AdminStudentController {

    private final UserService userService;

    /**
     * (Admin) Tìm kiếm, phân trang tất cả user
     * GET /admin/students?query=binh&role=student&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse> searchStudents(
            @Valid StudentSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        log.info("Admin tìm kiếm user với query: {}", searchRequest.getQuery());
        var studentPage = userService.searchStudents(searchRequest, pageable);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách user thành công")
                .data(studentPage)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getStudentDetails(@PathVariable Integer userId) {
        log.info("Admin lấy chi tiết User ID: {}", userId);
        var studentDetail = userService.getStudentDetails(userId);

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
            @Valid @RequestBody StudentUpdateRequestDTO updateRequestDTO
    ) {
        log.info("Admin cập nhật User ID: {}", userId);
        var updatedStudent = userService.updateStudent(userId, updateRequestDTO);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật user thành công")
                .data(updatedStudent)
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Integer userId) {
        log.info("Admin xóa User ID: {}", userId);
        userService.deleteStudent(userId);

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
            @Valid @RequestBody NotificationRequestDTO notificationRequestDTO
    ) {
        log.info("Admin gửi thông báo cho User ID: {}", userId);
        userService.sendNotificationToStudent(userId, notificationRequestDTO);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Gửi thông báo thành công")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
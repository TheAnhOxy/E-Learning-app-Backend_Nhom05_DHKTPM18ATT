package com.elearning.controller.admin;


import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; // Giả sử
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    private Integer getAuthenticatedUserId(UserDetails userDetails) {
        log.warn("ĐANG GẮN CỨNG USER ID = 3 (admin01) ĐỂ TEST");
        return 3;
    }
    private boolean isAdmin(UserDetails userDetails) {
        log.warn("ĐANG GẮN CỨNG ROLE = ADMIN ĐỂ TEST");
        return true;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        boolean isAdmin = isAdmin(userDetails);

        var stats = statisticsService.getDashboardStats(userId, isAdmin);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thống kê Dashboard thành công")
                .data(stats)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        boolean isAdmin = isAdmin(userDetails);
        Integer instructorId = isAdmin ? null : userId;

        var chartData = statisticsService.getRevenueChartData(startDate, endDate, instructorId);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy dữ liệu biểu đồ doanh thu thành công")
                .data(chartData)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy dữ liệu biểu đồ Học viên mới (cho trang Reports - Chỉ Admin)
     * GET /api/v1/statistics/reports/new-students?startDate=2025-01-01&endDate=2025-10-31
     */
    @GetMapping("/reports/new-students")
    // @PreAuthorize("hasRole('ADMIN')") // Thêm dòng này nếu bạn dùng Spring Security
    public ResponseEntity<ApiResponse> getNewStudentsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // (Chỉ Admin mới nên thấy)
        var chartData = statisticsService.getNewStudentsChartData(startDate, endDate);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy dữ liệu biểu đồ học viên mới thành công")
                .data(chartData)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy Top khóa học (cho Dashboard hoặc Reports)
     * GET /api/v1/statistics/reports/top-courses?by=revenue&limit=5
     * GET /api/v1/statistics/reports/top-courses?by=enrollment&limit=5
     */
    @GetMapping("/reports/top-courses")
    public ResponseEntity<ApiResponse> getTopCourses(
            @RequestParam(defaultValue = "revenue") String by, // 'revenue' hoặc 'enrollment'
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal UserDetails userDetails // Giả lập
    ) {
        Integer userId = getAuthenticatedUserId(userDetails);
        boolean isAdmin = isAdmin(userDetails);
        Integer instructorId = isAdmin ? null : userId;

        var topCourses = statisticsService.getTopCourses(by, limit, instructorId);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách top khóa học thành công")
                .data(topCourses)
                .build();
        return ResponseEntity.ok(response);
    }
}
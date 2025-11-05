package com.elearning.controller.admin;



import com.elearning.enums.UserRole;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.CustomUserDetails;
import com.elearning.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardStats(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
        boolean isAdmin = userDetails.getRole() == UserRole.admin;
        log.info("Lấy Dashboard Stats cho User ID: {}", userId);

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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getId();
        boolean isAdmin = userDetails.getRole() == UserRole.admin;
        Integer instructorId = isAdmin ? null : userId;

        var chartData = statisticsService.getRevenueChartData(startDate, endDate, instructorId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy dữ liệu biểu đồ doanh thu thành công")
                .data(chartData)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/new-students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getNewStudentsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        var chartData = statisticsService.getNewStudentsChartData(startDate, endDate);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy dữ liệu biểu đồ học viên mới thành công")
                .data(chartData)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/top-courses")
    // @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse> getTopCourses(
            @RequestParam(defaultValue = "revenue") String by,
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer instructorId = null;
        if (userDetails != null && userDetails.getRole() == UserRole.instructor) {
            instructorId = userDetails.getId();
        }

        var topCourses = statisticsService.getTopCourses(by, limit, instructorId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách top khóa học thành công")
                .data(topCourses)
                .build();
        return ResponseEntity.ok(response);
    }
}
package com.elearning.service.impl;


import com.elearning.enums.UserRole;
import com.elearning.modal.dto.response.DashboardStatsDTO;
import com.elearning.modal.dto.response.TimeSeriesDataDTO;
import com.elearning.modal.dto.response.TopCourseResponseDTO;
import com.elearning.repository.*;
import com.elearning.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public DashboardStatsDTO getDashboardStats(Integer userId, boolean isAdmin) {
        log.info("Lấy thống kê Dashboard cho User ID: {} (IsAdmin: {})", userId, isAdmin);
        if (isAdmin) {
            BigDecimal totalRevenue = transactionRepository.findTotalRevenue();
            Long totalStudents = userRepository.countByRole(UserRole.student);
            Long totalCourses = courseRepository.count();
            Long totalOrders = orderRepository.count();
            return DashboardStatsDTO.builder()
                    .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                    .totalStudents(totalStudents)
                    .totalCourses(totalCourses)
                    .totalOrders(totalOrders)
                    .build();
        } else {
            log.info("Lấy thống kê thật cho Instructor ID: {}", userId);

            BigDecimal totalRevenue = transactionRepository.findTotalRevenueByInstructor(userId);
            Long totalStudents = userRepository.countStudentsByInstructor(userId);
            Long totalCourses = courseRepository.countByInstructorId(userId);
            Long totalOrders = orderRepository.countByInstructorId(userId);

            return DashboardStatsDTO.builder()
                    .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                    .totalStudents(totalStudents)
                    .totalCourses(totalCourses)
                    .totalOrders(totalOrders)
                    .build();
        }
        }


    @Override
    public List<TimeSeriesDataDTO> getRevenueChartData(LocalDate startDate, LocalDate endDate, Integer instructorId) {
        log.info("Lấy dữ liệu biểu đồ doanh thu từ {} đến {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> rawData;
        if (instructorId != null) {
            // --- Logic cho INSTRUCTOR (ĐÃ SỬA) ---
            log.debug("Lấy doanh thu cho Instructor ID: {}", instructorId);
            rawData = transactionRepository.getRevenueStatsByDateRangeAndInstructor(startDateTime, endDateTime, instructorId);
        } else {
            // --- Logic cho ADMIN ---
            log.debug("Lấy doanh thu cho Admin");
            rawData = transactionRepository.getRevenueStatsByDateRange(startDateTime, endDateTime);
        }

        return rawData.stream()
                .map(row -> new TimeSeriesDataDTO(
                        ((java.sql.Date) row[0]).toLocalDate(), // Sửa: Dùng java.sql.Date
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesDataDTO> getNewStudentsChartData(LocalDate startDate, LocalDate endDate) {
        log.info("Lấy dữ liệu biểu đồ học viên mới từ {} đến {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> rawData = userRepository.getNewStudentStatsByDateRange(startDateTime, endDateTime);

        return rawData.stream()
                .map(row -> new TimeSeriesDataDTO(
                        ((java.sql.Date) row[0]).toLocalDate(), // Sửa: Dùng java.sql.Date
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TopCourseResponseDTO> getTopCourses(String criteria, int limit, Integer instructorId) {
        log.info("Lấy top {} khóa học theo: {}", limit, criteria);
        Pageable pageable = PageRequest.of(0, limit);

        // --- Logic cho INSTRUCTOR (ĐÃ SỬA) ---
        if ("revenue".equalsIgnoreCase(criteria)) {
            if (instructorId != null) {
                return courseRepository.findTopCoursesByRevenueAndInstructor(instructorId, pageable);
            } else {
                return courseRepository.findTopCoursesByRevenue(pageable);
            }
        } else {
            if (instructorId != null) {
                return courseRepository.findTopCoursesByEnrollmentAndInstructor(instructorId, pageable);
            } else {
                return courseRepository.findTopCoursesByEnrollment(pageable);
            }
        }
    }

}
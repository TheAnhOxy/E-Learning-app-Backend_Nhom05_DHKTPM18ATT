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
            log.warn("Đang trả về thống kê giả lập cho Instructor.");
            return DashboardStatsDTO.builder()
                    .totalRevenue(new BigDecimal("15000000"))
                    .totalStudents(150L)
                    .totalCourses(5L)
                    .totalOrders(80L)
                    .build();
        }
    }

    @Override
    public List<TimeSeriesDataDTO> getRevenueChartData(LocalDate startDate, LocalDate endDate, Integer instructorId) {
        log.info("Lấy dữ liệu biểu đồ doanh thu từ {} đến {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        if (instructorId != null) {
            log.warn("Đang trả về dữ liệu doanh thu giả lập cho Instructor.");
            return List.of();
        } else {
            List<Object[]> rawData = transactionRepository.getRevenueStatsByDateRange(startDateTime, endDateTime);

            return rawData.stream()
                    .map(row -> new TimeSeriesDataDTO(
                            (LocalDate) row[0],
                            (BigDecimal) row[1]
                    ))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TimeSeriesDataDTO> getNewStudentsChartData(LocalDate startDate, LocalDate endDate) {
        log.info("Lấy dữ liệu biểu đồ học viên mới từ {} đến {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> rawData = userRepository.getNewStudentStatsByDateRange(startDateTime, endDateTime);

        return rawData.stream()
                .map(row -> new TimeSeriesDataDTO(
                        (LocalDate) row[0], // [00] là CAST(u.createdAt AS DATE)
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TopCourseResponseDTO> getTopCourses(String criteria, int limit, Integer instructorId) {
        log.info("Lấy top {} khóa học theo: {}", limit, criteria);
        Pageable pageable = PageRequest.of(0, limit);
        if ("revenue".equalsIgnoreCase(criteria)) {
            return courseRepository.findTopCoursesByRevenue(pageable);
        } else {
            return courseRepository.findTopCoursesByEnrollment(pageable);
        }
    }
}
package com.elearning.service;


import com.elearning.modal.dto.response.DashboardStatsDTO;
import com.elearning.modal.dto.response.TimeSeriesDataDTO;
import com.elearning.modal.dto.response.TopCourseResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {


    DashboardStatsDTO getDashboardStats(Integer userId, boolean isAdmin);

    List<TimeSeriesDataDTO> getRevenueChartData(LocalDate startDate, LocalDate endDate, Integer instructorId);
    List<TimeSeriesDataDTO> getNewStudentsChartData(LocalDate startDate, LocalDate endDate);


    List<TopCourseResponseDTO> getTopCourses(String criteria, int limit, Integer instructorId);
}
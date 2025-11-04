package com.elearning.service;

import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.CourseResponseDTO;
import com.elearning.modal.dto.search.CourseSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO);
    Page<CourseResponseDTO> searchCourses(CourseSearchRequest searchRequest, Pageable pageable);
    CourseResponseDTO getCourseById(Integer id);
    CourseResponseDTO updateCourse(Integer id, CourseRequestDTO courseRequestDTO);
    void deleteCourse(Integer id);
}
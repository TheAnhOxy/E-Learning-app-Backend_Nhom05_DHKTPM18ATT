package com.elearning.controller;

import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.search.CourseSearchRequest;
import com.elearning.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCourse(@Valid @RequestBody CourseRequestDTO courseRequestDTO) {
        log.info("Nhận yêu cầu tạo khóa học mới: {}", courseRequestDTO.getTitle());
        var courseData = courseService.createCourse(courseRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo khóa học thành công!")
                .data(courseData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> searchCourses(
            @Valid CourseSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        log.info("Nhận yêu cầu tìm kiếm khóa học với tiêu đề: {}", searchRequest.getTitle());
        var coursePageData = courseService.searchCourses(searchRequest, pageable);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm khóa học thành công")
                .data(coursePageData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCourseById(@PathVariable Integer id) {
        log.info("Nhận yêu cầu lấy chi tiết khóa học ID: {}", id);
        var courseData = courseService.getCourseById(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết khóa học thành công")
                .data(courseData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseRequestDTO courseRequestDTO
    ) {
        log.info("Nhận yêu cầu cập nhật khóa học ID: {}", id);
        var updatedCourseData = courseService.updateCourse(id, courseRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật khóa học thành công")
                .data(updatedCourseData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable Integer id) {
        log.info("Nhận yêu cầu xóa khóa học ID: {}", id);
        courseService.deleteCourse(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa khóa học thành công")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
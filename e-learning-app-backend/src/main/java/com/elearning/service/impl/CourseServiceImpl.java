package com.elearning.service.impl;

import com.elearning.converter.CourseConverter;

import com.elearning.entity.Course;
import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.CourseResponseDTO;
import com.elearning.modal.dto.search.CourseSearchRequest;
import com.elearning.repository.CourseRepository;
import com.elearning.service.CourseService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;

    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        log.info("Bắt đầu tạo khóa học với tiêu đề: {}", courseRequestDTO.getTitle());
        Course course = courseConverter.toEntity(courseRequestDTO);
        Course savedCourse = courseRepository.save(course);
        log.info("Đã lưu khóa học thành công với ID: {}", savedCourse.getId());
        return courseConverter.toDTO(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> searchCourses(CourseSearchRequest searchRequest, Pageable pageable) {
        log.info("Đang tìm kiếm khóa học...");

        Specification<Course> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchRequest.getTitle())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + searchRequest.getTitle().toLowerCase() + "%"
                ));
            }

            if (searchRequest.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("category").get("id"),
                        searchRequest.getCategoryId()
                ));
            }

            if (searchRequest.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        searchRequest.getStatus()
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };


        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        log.info("Đã tìm thấy {} khóa học.", coursePage.getTotalElements());

        return coursePage.map(courseConverter::toDTO);
    }
}

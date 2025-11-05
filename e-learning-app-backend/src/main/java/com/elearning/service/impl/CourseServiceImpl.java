package com.elearning.service.impl;

import com.elearning.converter.CourseConverter;
import com.elearning.converter.MyCourseConverter;
import com.elearning.entity.Course;
import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.CourseResponseDTO;
import com.elearning.modal.dto.response.MyCourseResponseDTO;
import com.elearning.modal.dto.search.CourseSearchRequest;
import com.elearning.repository.CourseRepository;
import com.elearning.service.CourseService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final MyCourseConverter myCourseConverter;

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

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Integer id) {
        log.info("Đang tìm khóa học với ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow();
        log.info("Đã tìm thấy khóa học: {}", course.getTitle());
        return courseConverter.toDTO(course);
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourse(Integer id, CourseRequestDTO courseRequestDTO) {
        log.info("Đang cập nhật khóa học ID: {}", id);

        Course existingCourse = courseRepository.findById(id)
                .orElseThrow();
        courseConverter.updateEntity(existingCourse, courseRequestDTO);
        Course updatedCourse = courseRepository.save(existingCourse);
        log.info("Đã cập nhật khóa học thành công.");
        return courseConverter.toDTO(updatedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Integer id) {
        log.info("Đang xóa khóa học ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow();
        courseRepository.delete(course);
        log.info("Đã xóa khóa học thành công.");
    }

    //---------------------------------------------------------------------

    @Override
    public List<CourseResponseDTO> getRecommendedCourses() {
        log.info("Đang lấy danh sách khóa học được đề xuất.");
        List<Course> recommendedCourses = courseRepository.findTop10ByPriceGreaterThanOrderByIdAsc(0);
        log.info("Đã lấy được {} khóa học được đề xuất.", recommendedCourses.size());
        return recommendedCourses.stream()
                .map(courseConverter::toDTO)
                .toList();
    }

    @Override
    public List<CourseResponseDTO> getPopularCourses() {
        log.info("Đang lấy danh sách khóa học phổ biến.");
        List<Course> popularCourses = courseRepository.findTop10ByEnrollmentsCountDesc();
        log.info("Đã lấy được {} khóa học phổ biến.", popularCourses.size());
        return popularCourses.stream()
                .map(courseConverter::toDTO)
                .toList();
    }

    @Override
    public List<CourseResponseDTO> getInspiringCourses() {
        log.info("Đang lấy danh sách khóa học truyền cảm hứng.");
        List<Course> inspiringCourses = courseRepository.findTop10ByAverageRatingDesc();
        log.info("Đã lấy được {} khóa học truyền cảm hứng.", inspiringCourses.size());
        return inspiringCourses.stream()
                .map(courseConverter::toDTO)
                .toList();
    }

    @Override
    public Page<CourseResponseDTO> getCoursesByCategoryId(Integer categoryId, int page, int limit) {
        log.info("Đang lấy danh sách khóa học theo categoryId = {} (page={}, limit={})", categoryId, page, limit);

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Course> coursePage = courseRepository.findByCategory_Id(categoryId, pageable);

        List<CourseResponseDTO> courseDTOs = coursePage.getContent()
                .stream()
                .map(courseConverter::toDTO)
                .toList();

        Page<CourseResponseDTO> dtoPage = new PageImpl<>(courseDTOs, pageable, coursePage.getTotalElements());

        log.info("Đã lấy được {} khóa học trong danh mục {}", courseDTOs.size(), categoryId);
        return dtoPage;
    }

    @Override
    public Page<CourseResponseDTO> getCourseByTeacherId(Integer instructorId, int page, int limit) {
        log.info("Đang lấy danh sách khóa học theo instructorId = {} (page={}, limit={})", instructorId, page, limit);

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Course> coursePage = courseRepository.findByInstructor_Id(instructorId, pageable);

        List<CourseResponseDTO> courseDTOs = coursePage.getContent()
                .stream()
                .map(courseConverter::toDTO)
                .toList();

        Page<CourseResponseDTO> dtoPage = new PageImpl<>(courseDTOs, pageable, coursePage.getTotalElements());

        log.info("Đã lấy được {} khóa học của giảng viên {}", courseDTOs.size(), instructorId);
        return dtoPage;
    }

    @Override
    public Page<MyCourseResponseDTO> getCoursesByStudentId(Integer userId, int page, int limit) {
        log.info("Đang lấy danh sách khóa học theo studentId = {} (page={}, limit={})", userId, page, limit);

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Course> coursePage = courseRepository.findCoursesByStudentId(userId, pageable);

        List<MyCourseResponseDTO> myCourseDTOs = coursePage.getContent()
                .stream()
                .map(course -> myCourseConverter.toDTO(course, userId))
                .toList();

        Page<MyCourseResponseDTO> dtoPage = new PageImpl<>(myCourseDTOs, pageable, coursePage.getTotalElements());

        log.info("Đã lấy được {} khóa học đã mua của học viên {}", myCourseDTOs.size(), userId);
        return dtoPage;
    }

}
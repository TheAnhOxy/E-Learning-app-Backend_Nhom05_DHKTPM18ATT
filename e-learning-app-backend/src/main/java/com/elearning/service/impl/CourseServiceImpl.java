package com.elearning.service.impl;

import com.elearning.converter.CourseConverter;
import com.elearning.entity.Course;
import com.elearning.entity.Lesson;
import com.elearning.entity.Quiz;
import com.elearning.entity.Section;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.CourseRequestDTO;
import com.elearning.modal.dto.response.*;
import com.elearning.modal.dto.search.CourseSearchRequest;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.service.CourseService;
import com.elearning.service.ReviewService;
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
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseConverter courseConverter;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewService reviewService;

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
    @Transactional
    public CourseDetailResponseDTO getCourseDetailById(Integer id) {
        log.info("Lấy chi tiết khóa học (detail) ID: {}", id);

        Course course = courseRepository.findByIdWithSectionsLessonsQuizzes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        CourseDetailResponseDTO dto = new CourseDetailResponseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setPrice(course.getPrice());
        dto.setOriginalPrice(course.getOriginalPrice());
        dto.setStatus(course.getStatus());
        dto.setVideoPreviewUrl(course.getVideoPreviewUrl());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setInstructorId(course.getInstructor() != null ? course.getInstructor().getId() : null);
        dto.setInstructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : null);
        dto.setCategoryId(course.getCategory() != null ? course.getCategory().getId() : null);
        dto.setCategoryName(course.getCategory() != null ? course.getCategory().getName() : null);

        // ✅ Sections -> Lessons -> Quizzes -> Questions -> Answers
        List<SectionResponseDTO> sectionDTOs = new ArrayList<>();
        if (course.getSections() != null && !course.getSections().isEmpty()) {
            List<Section> sections = new ArrayList<>(course.getSections());
            sections.sort(Comparator.comparing(s -> s.getOrderIndex() == null ? 0 : s.getOrderIndex()));

            for (Section section : sections) {
                SectionResponseDTO sectionDTO = new SectionResponseDTO();
                sectionDTO.setId(section.getId());
                sectionDTO.setTitle(section.getTitle());
                sectionDTO.setOrderIndex(section.getOrderIndex());
                sectionDTO.setCourseId(course.getId());

                List<LessonResponseDTO> lessonDTOs = new ArrayList<>();
                if (section.getLessons() != null && !section.getLessons().isEmpty()) {
                    List<Lesson> lessons = new ArrayList<>(section.getLessons());
                    lessons.sort(Comparator.comparing(l -> l.getOrderIndex() == null ? 0 : l.getOrderIndex()));

                    for (Lesson lesson : lessons) {
                        LessonResponseDTO lessonDTO = new LessonResponseDTO();
                        lessonDTO.setId(lesson.getId());
                        lessonDTO.setTitle(lesson.getTitle());
                        lessonDTO.setProvider(lesson.getProvider());
                        lessonDTO.setPlaybackUrl(lesson.getPlaybackUrl());
                        lessonDTO.setVideoDescription(lesson.getVideoDescription());
                        lessonDTO.setDurationInSeconds(lesson.getDurationInSeconds());
                        lessonDTO.setSectionId(section.getId());
                        lessonDTO.setViewsCount(lesson.getViewsCount());
                        lessonDTO.setIsFree(lesson.getIsFree());
                        lessonDTO.setOrderIndex(lesson.getOrderIndex());

                        // ✅ Map quizzes
                        if (lesson.getQuizzes() != null && !lesson.getQuizzes().isEmpty()) {
                            List<QuizResponseDTO> quizDTOs = lesson.getQuizzes().stream().map(quiz -> {
                                QuizResponseDTO quizDTO = new QuizResponseDTO();
                                quizDTO.setId(quiz.getId());
                                quizDTO.setTitle(quiz.getTitle());
                                quizDTO.setLessonId(lesson.getId());
                                quizDTO.setCreatedAt(quiz.getCreatedAt());

                                // ✅ Map questions
                                if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                                    List<QuestionResponseDTO> questionDTOs = quiz.getQuestions().stream().map(q -> {
                                        QuestionResponseDTO questionDTO = new QuestionResponseDTO();
                                        questionDTO.setId(q.getId());
                                        questionDTO.setQuestionText(q.getQuestionText());
                                        questionDTO.setQuestionType(q.getQuestionType());

                                        // ✅ Map answers
                                        if (q.getAnswers() != null && !q.getAnswers().isEmpty()) {
                                            List<AnswerResponseDTO> answerDTOs = q.getAnswers().stream().map(a ->
                                                    new AnswerResponseDTO(
                                                            a.getId(),
                                                            a.getAnswerText(),
                                                            a.getIsCorrect()
                                                    )
                                            ).toList();
                                            questionDTO.setAnswers(answerDTOs);
                                        }

                                        return questionDTO;
                                    }).toList();
                                    quizDTO.setQuestions(questionDTOs);
                                }

                                return quizDTO;
                            }).toList();

                            lessonDTO.setQuizzes(quizDTOs);
                        }

                        lessonDTOs.add(lessonDTO);
                    }
                }
                sectionDTO.setLessons(lessonDTOs);
                sectionDTOs.add(sectionDTO);
            }
        }
        dto.setSections(sectionDTOs);

        List<ReviewResponseDTO> reviews = reviewService.getReviewsByCourseId(id);
        dto.setReviews(reviews);

        int studentCount = enrollmentRepository.countStudentsByCourse(id);
        dto.setStudentCount(studentCount);

        log.info("Khoá học ID {} có {} học viên đăng ký", id, studentCount);

        return dto;
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
}
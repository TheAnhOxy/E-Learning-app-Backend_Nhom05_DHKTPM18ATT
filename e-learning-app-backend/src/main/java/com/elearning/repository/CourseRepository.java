package com.elearning.repository;

import com.elearning.entity.Course;
import com.elearning.modal.dto.response.TopCourseResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {

    Long countByInstructorId(Integer instructorId);
    @Query("SELECT NEW com.elearning.modal.dto.response.TopCourseResponseDTO(c.id, c.title, c.thumbnailUrl, SUM(t.amount)) " +
            "FROM Transaction t " +
            "JOIN t.order o " +
            "JOIN o.course c " +
            "WHERE t.status = 'success' AND t.amount > 0 " +
            "GROUP BY c.id, c.title, c.thumbnailUrl " +
            "ORDER BY SUM(t.amount) DESC")
    List<TopCourseResponseDTO> findTopCoursesByRevenue(Pageable pageable);
    List<Course> findAllByInstructorId(Integer instructorId);

    @Query("SELECT new com.elearning.modal.dto.response.TopCourseResponseDTO(c.id, c.title, c.thumbnailUrl, COUNT(e.id)) " +
            "FROM Enrollment e " +
            "JOIN e.course c " +
            "GROUP BY c.id, c.title, c.thumbnailUrl " +
            "ORDER BY COUNT(e.id) DESC")
    List<TopCourseResponseDTO> findTopCoursesByEnrollment(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.sections s " +
            "LEFT JOIN FETCH s.lessons l " +
            "LEFT JOIN FETCH l.quizzes q " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithSectionsLessonsQuizzes(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"sections", "sections.lessons"})
    Optional<Course> findDetailedById(Integer id);
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    int countStudentsByCourse(@Param("courseId") Integer courseId);

    @Query("SELECT NEW com.elearning.modal.dto.response.TopCourseResponseDTO(c.id, c.title, c.thumbnailUrl, SUM(t.amount)) " +
            "FROM Transaction t " +
            "JOIN t.order o " +
            "JOIN o.course c " +
            "WHERE t.status = 'success' AND t.amount > 0 AND c.instructor.id = :instructorId " +
            "GROUP BY c.id, c.title, c.thumbnailUrl " +
            "ORDER BY SUM(t.amount) DESC")
    List<TopCourseResponseDTO> findTopCoursesByRevenueAndInstructor(@Param("instructorId") Integer instructorId, Pageable pageable);

    @Query("SELECT NEW com.elearning.modal.dto.response.TopCourseResponseDTO(c.id, c.title, c.thumbnailUrl, COUNT(e.id)) " +
            "FROM Enrollment e " +
            "JOIN e.course c " +
            "WHERE c.instructor.id = :instructorId " +
            "GROUP BY c.id, c.title, c.thumbnailUrl " +
            "ORDER BY COUNT(e.id) DESC")
    List<TopCourseResponseDTO> findTopCoursesByEnrollmentAndInstructor(@Param("instructorId") Integer instructorId, Pageable pageable);




}
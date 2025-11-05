package com.elearning.repository;

import com.elearning.entity.Course;
import com.elearning.modal.dto.response.TopCourseResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {


    @Query("SELECT new com.elearning.modal.dto.response.TopCourseResponseDTO(c.id, c.title, c.thumbnailUrl, SUM(t.amount)) " +
            "FROM Transaction t " +
            "JOIN t.order o " +
            "JOIN o.course c " +
            "WHERE t.status = 'success' " +
            "GROUP BY c.id, c.title, c.thumbnailUrl " +
            "ORDER BY SUM(t.amount) DESC")
    List<TopCourseResponseDTO> findTopCoursesByRevenue(Pageable pageable);


    @Query("SELECT new com.elearning.modal.dto.response.TopCourseResponseDTO(c.id, c.title, c.thumbnailUrl, COUNT(e.id)) " +
            "FROM Enrollment e " +
            "JOIN e.course c " +
            "GROUP BY c.id, c.title, c.thumbnailUrl " +
            "ORDER BY COUNT(e.id) DESC")
    List<TopCourseResponseDTO> findTopCoursesByEnrollment(Pageable pageable);

}
package com.elearning.repository;

import com.elearning.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    boolean existsByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    boolean existsByUserIdAndCourseId(Integer userId, Integer courseId);

    Enrollment findByOrderId(Integer orderId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course c WHERE e.user.id = :userId")
    List<Enrollment> findAllWithCourseByUserId(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT e.user.id FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    List<Integer> findStudentIdsByInstructorId(@Param("instructorId") Integer instructorId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    int countByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    int countStudentsByCourse(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e " +
            "WHERE e.user.id = :studentId " +
            "AND e.course.instructor.id = :instructorId")
    boolean isStudentEnrolledInInstructorCourses(@Param("studentId") Integer studentId,
                                                 @Param("instructorId") Integer instructorId);

    Optional<Enrollment> findByUserIdAndCourseId(Integer userId, Integer courseId);
}

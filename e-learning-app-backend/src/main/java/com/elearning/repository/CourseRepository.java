package com.elearning.repository;

import com.elearning.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {
    // Khóa học được đề xuất
    List<Course> findTop10ByPriceGreaterThanOrderByIdAsc(int price);

    // Khóa học phổ biến, join với bảng đăng ký
    @Query("SELECT c FROM Course c JOIN c.enrollments e GROUP BY c.id ORDER BY COUNT(e.id) DESC")
    List<Course> findTop10ByEnrollmentsCountDesc();

    // Khóa học truyền cảm hứng, ví dụ: khóa học có đánh giá cao nhất, join với bảng đánh giá
    @Query("SELECT c FROM Course c JOIN c.reviews r GROUP BY c.id ORDER BY AVG(r.rating) DESC")
    List<Course> findTop10ByAverageRatingDesc();

    Page<Course> findByCategory_Id(Integer categoryId, Pageable pageable);
    Page<Course> findByInstructor_Id(Integer instructorId, Pageable pageable);

    // Lấy khóa học của một học viên cụ thể
    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.user.id = :studentId")
    Page<Course> findCoursesByStudentId(Integer studentId, Pageable pageable);
}
package com.elearning.repository;

import com.elearning.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Integer> {
    // List<Section> findAllByCourseIdOrderByOrderIndexAsc(Integer courseId);
}
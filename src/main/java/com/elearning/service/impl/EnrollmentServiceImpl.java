package com.elearning.service.impl;

import com.elearning.repository.EnrollmentRepository;
import com.elearning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;


    @Override
    public boolean isEnrolled(Integer userId, Integer courseId) {
        if (userId == null || courseId == null) {
            log.warn("Thiếu userId hoặc courseId khi kiểm tra enrollment");
            return false;
        }

        boolean enrolled = enrollmentRepository.existsByUser_IdAndCourse_Id(userId, courseId);
        log.info("Kiểm tra enrollment: userId={}, courseId={} → {}", userId, courseId, enrolled);
        return enrolled;
    }
}

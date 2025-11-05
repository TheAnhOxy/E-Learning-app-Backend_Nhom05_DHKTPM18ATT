package com.elearning.service.impl;

import com.elearning.converter.UserConverter;
import com.elearning.entity.Certificate;
import com.elearning.entity.Enrollment;
import com.elearning.entity.QuizAttempt;
import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import com.elearning.exception.ForBiddenException;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.NotificationRequestDTO;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.response.StudentDetailResponseDTO;
import com.elearning.modal.dto.response.StudentResponseDTO;
import com.elearning.modal.dto.response.UserResponseDTO;
import com.elearning.modal.dto.search.StudentSearchRequest;
import com.elearning.repository.CertificateRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.QuizAttemptRepository;
import com.elearning.repository.UserRepository;
import com.elearning.service.NotificationService;
import com.elearning.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    private final NotificationService notificationService;

    private final EnrollmentRepository enrollmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CertificateRepository certificateRepository;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.info("Đang lấy tất cả user từ repository...");

        List<User> users = userRepository.findAll(Sort.by("id").ascending());
        log.info("Đã lấy {} users, đang chuyển đổi sang DTO...", users.size());
        return users.stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserEntityById(Integer id) {
        log.debug("Đang tìm User entity với ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudents(StudentSearchRequest request, Pageable pageable) {
        log.info("Admin tìm kiếm user/student...");

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getQuery())) {
                String queryLower = "%" + request.getQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), queryLower),
                        cb.like(cb.lower(root.get("fullName")), queryLower),
                        cb.like(cb.lower(root.get("email")), queryLower)
                ));
            }
            if (request.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), request.getRole()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);
        log.info("Tìm thấy {} user.", userPage.getTotalElements());
        return userPage.map(userConverter::toStudentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDetailResponseDTO getStudentDetails(Integer userId) {
        log.info("Admin lấy chi tiết cho User ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findAllWithCourseByUserId(userId);
        List<QuizAttempt> quizAttempts = quizAttemptRepository.findAllWithQuizByUserId(userId);
        List<Certificate> certificates = certificateRepository.findAllWithCourseByUserId(userId);
        return userConverter.toStudentDetailDTO(user, enrollments, quizAttempts, certificates);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(Integer userId, StudentUpdateRequestDTO dto) {
        log.info("Admin cập nhật User ID: {}", userId);
        User user = getUserEntityById(userId);
        userConverter.updateEntity(user, dto);
        User updatedUser = userRepository.save(user);

        return userConverter.toStudentDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteStudent(Integer userId) {
        log.info("Admin xóa User ID: {}", userId);
        User user = getUserEntityById(userId);
        if (user.getRole() == UserRole.admin) {
            throw new ForBiddenException("Không thể xóa tài khoản Admin.");
        }

        // (Logic nghiệp vụ: Kiểm tra xem user có phải là instructor
        // đang dạy khóa học nào không...)

        // DB đã thiết lập ON DELETE CASCADE/SET NULL, nên chỉ cần xóa user
        userRepository.delete(user);
        log.info("Đã xóa User ID: {}", userId);
    }

    @Override
    @Transactional
    public void sendNotificationToStudent(Integer userId, NotificationRequestDTO dto) {
        log.info("Admin gửi thông báo cho User ID: {}", userId);
        User user = getUserEntityById(userId);
        notificationService.sendNotification(user, dto.getTitle(), dto.getMessage());
        log.info("Đã gửi thông báo thành công.");
    }
}
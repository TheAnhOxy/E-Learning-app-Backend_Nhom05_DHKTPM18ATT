package com.elearning.service.impl;

import com.elearning.converter.UserConverter;
import com.elearning.entity.User;
import com.elearning.enums.UserRole;

import com.elearning.entity.*;
import com.elearning.enums.UserRole;
import com.elearning.exception.ConflictException;
import com.elearning.exception.ForBiddenException;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.NotificationRequestDTO;
import com.elearning.modal.dto.request.RegisterRequestDTO;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.request.UserRequestDTO;
import com.elearning.modal.dto.response.CourseBriefResponseDTO;
import com.elearning.modal.dto.response.StudentDetailResponseDTO;
import com.elearning.modal.dto.response.StudentResponseDTO;
import com.elearning.modal.dto.response.UserResponseDTO;
import com.elearning.modal.dto.search.StudentSearchRequest;
import com.elearning.repository.*;
import com.elearning.service.NotificationService;
import com.elearning.service.UserService;
import com.elearning.service.CustomUserDetails;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final TransactionRepository transactionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CertificateRepository certificateRepository;


    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(Integer id) {
        log.debug("Đang tìm User entity với ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow();
    }


    @Override
    public List<UserResponseDTO> getTopInstructors() {
        log.info("Đang lấy top instructors...");
        List<User> instructors = userRepository.findTop15ByRoleOrderByIdAsc(UserRole.instructor);
        log.info("Đã lấy {} instructors.", instructors.size());
        return instructors.stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getTeacherById(Integer id) {
        return userRepository.findByIdAndRole(id, UserRole.instructor)
                .map(userConverter::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudents(StudentSearchRequest request, Pageable pageable, CustomUserDetails currentUser) {
        log.info("User ID {} (Role: {}) đang tìm kiếm user/student...", currentUser.getId(), currentUser.getRole());

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
            } else {
                predicates.add(cb.equal(root.get("role"), UserRole.student));
            }
            if (currentUser.getRole() == UserRole.instructor) {
                log.debug("Phân quyền Instructor: Chỉ tìm student của instructor ID: {}", currentUser.getId());

                List<Integer> myStudentIds = enrollmentRepository.findStudentIdsByInstructorId(currentUser.getId());

                if (myStudentIds.isEmpty()) {
                    log.warn("Instructor ID {} không có học viên nào.", currentUser.getId());
                    predicates.add(cb.disjunction());
                } else {
                    predicates.add(root.get("id").in(myStudentIds));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);
        log.info("Tìm thấy {} user.", userPage.getTotalElements());

        return userPage.map(userConverter::toStudentDTO);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public StudentDetailResponseDTO getStudentDetails(Integer userId, CustomUserDetails currentUser) {
//        log.info("User ID {} (Role: {}) đang lấy chi tiết cho User ID: {}", currentUser.getId(), currentUser.getRole(), userId);
//
//        User user = getUserEntityById(userId);
//        boolean isGettingSelf = currentUser.getId().equals(userId);
//        if (currentUser.getRole() == UserRole.instructor && !isGettingSelf) {
//            boolean isMyStudent = enrollmentRepository.isStudentEnrolledInInstructorCourses(userId, currentUser.getId());
//            if (!isMyStudent) {
//                log.warn("Cảnh báo: Instructor ID {} cố gắng xem chi tiết Student ID {} (không phải học viên của họ)", currentUser.getId(), userId);
//                throw new ForBiddenException("Bạn không có quyền xem chi tiết học viên này.");
//            }
//        }
//
//        List<Enrollment> enrollments = enrollmentRepository.findAllWithCourseByUserId(userId);
//        List<QuizAttempt> quizAttempts = quizAttemptRepository.findAllWithQuizByUserId(userId);
//        List<Certificate> certificates = certificateRepository.findAllWithCourseByUserId(userId);
//
//        return userConverter.toStudentDetailDTO(user, enrollments, quizAttempts, certificates);
//    }

    @Override
    @Transactional(readOnly = true)
    public StudentDetailResponseDTO getStudentDetails(Integer userId, CustomUserDetails currentUser) {
        log.info("User ID {} (Role: {}) đang lấy chi tiết cho User ID: {}", currentUser.getId(), currentUser.getRole(), userId);
        User user = getUserEntityById(userId);

        if (currentUser.getRole() == UserRole.instructor) {
            boolean isMyStudent = enrollmentRepository.isStudentEnrolledInInstructorCourses(userId, currentUser.getId());
            boolean isMyself = userId.equals(currentUser.getId());
            if (!isMyStudent && !isMyself) {
                throw new ForBiddenException("Bạn không có quyền xem chi tiết tài khoản này.");
            }
        }

        // --- SỬA LỖI LOGIC TẠI ĐÂY ---
        // 1. Lấy tất cả dữ liệu (Enrollments, Attempts, Certificates)
        List<Enrollment> enrollments = enrollmentRepository.findAllWithCourseByUserId(userId);
        List<QuizAttempt> quizAttempts = quizAttemptRepository.findAllWithQuizByUserId(userId);
        List<Certificate> certificates = certificateRepository.findAllWithCourseByUserId(userId);

        StudentDetailResponseDTO dto = userConverter.toStudentDetailDTO(user, enrollments, quizAttempts, certificates);
        if (user.getRole() == UserRole.instructor) {
            log.info("Lấy thống kê cho Giảng viên ID: {}", userId);
            BigDecimal totalRevenue = transactionRepository.findTotalRevenueByInstructor(userId);
            Long totalStudents = userRepository.countStudentsByInstructor(userId);
            Long totalCourses = courseRepository.countByInstructorId(userId);

            dto.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            dto.setTotalStudentsTaught(totalStudents);
            dto.setTotalCoursesCreated(totalCourses);

            log.info("Lấy danh sách khóa học đã tạo cho Giảng viên ID: {}", userId);
            List<Course> courses = courseRepository.findAllByInstructorId(userId);
            List<CourseBriefResponseDTO> courseDTOs = courses.stream()
                    .map(course -> CourseBriefResponseDTO.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .thumbnailUrl(course.getThumbnailUrl())
                            .status(course.getStatus() != null ? course.getStatus().name() : "DRAFT")
                            .build())
                    .collect(Collectors.toList());
            dto.setCreatedCourses(courseDTOs);
        }

        return dto;
    }


    @Override
    @Transactional
    public StudentResponseDTO createUser(UserRequestDTO dto, CustomUserDetails currentUser) {
        log.info("User ID {} (Role: {}) đang tạo user mới với email: {}", currentUser.getId(), currentUser.getRole(), dto.getEmail());
        if (currentUser.getRole() != UserRole.admin) {
            throw new ForBiddenException("Chỉ Admin mới có quyền tạo tài khoản.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email đã tồn tại.");
        }
        if (StringUtils.hasText(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("Username đã tồn tại.");
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setUsername(StringUtils.hasText(dto.getUsername()) ? dto.getUsername() : null);
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setRole(dto.getRole());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Đã tạo user mới thành công, ID: {}", savedUser.getId());

        return userConverter.toStudentDTO(savedUser);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(Integer userId, StudentUpdateRequestDTO dto, CustomUserDetails currentUser) {
        log.info("User ID {} (Role: {}) đang cập nhật User ID: {}", currentUser.getId(), currentUser.getRole(), userId);
        User user = getUserEntityById(userId);
        boolean isUpdatingSelf = currentUser.getId().equals(userId);
        if (currentUser.getRole() == UserRole.instructor && !isUpdatingSelf) {
            if (user.getRole() != UserRole.student) {
                throw new ForBiddenException("Bạn không có quyền chỉnh sửa tài khoản này.");
            }
            boolean isMyStudent = enrollmentRepository.isStudentEnrolledInInstructorCourses(userId, currentUser.getId());
            if (!isMyStudent) {
                throw new ForBiddenException("Bạn không có quyền sửa thông tin học viên này.");
            }
            if (dto.getRole() != UserRole.student) {
                throw new ForBiddenException("Bạn không được phép thay đổi vai trò của học viên.");
            }
        }

        userConverter.updateEntity(user, dto);
        User updatedUser = userRepository.save(user);

        return userConverter.toStudentDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteStudent(Integer userId, CustomUserDetails currentUser) {
        log.info("User ID {} (Role: {}) đang xóa User ID: {}", currentUser.getId(), currentUser.getRole(), userId);
        User user = getUserEntityById(userId);
        if (currentUser.getRole() != UserRole.admin) {
            throw new ForBiddenException("Chỉ Admin mới có quyền xóa tài khoản.");
        }
        if (user.getRole() == UserRole.admin) {
            throw new ForBiddenException("Không thể xóa tài khoản Admin khác.");
        }

        userRepository.delete(user);
        log.info("Đã xóa User ID: {}", userId);
    }

    @Override
    @Transactional
    public void sendNotificationToStudent(Integer userId, NotificationRequestDTO dto, CustomUserDetails currentUser) {
        log.info("User ID {} (Role: {}) gửi thông báo cho User ID: {}", currentUser.getId(), currentUser.getRole(), userId);
        User user = getUserEntityById(userId);

        if (currentUser.getRole() == UserRole.instructor) {
            boolean isMyStudent = enrollmentRepository.isStudentEnrolledInInstructorCourses(userId, currentUser.getId());
            if (!isMyStudent) {
                throw new ForBiddenException("Bạn không có quyền gửi thông báo cho học viên này.");
            }
        }
        notificationService.sendNotification(user, dto.getTitle(), dto.getMessage());
        log.info("Đã gửi thông báo thành công.");
    }

    @Override
    public User registerStudent(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setRole(UserRole.student);
        user.setAvatarUrl(request.getAvatarUrl() != null ? request.getAvatarUrl() : "https://via.placeholder.com/50");

        return userRepository.save(user);
    }
}
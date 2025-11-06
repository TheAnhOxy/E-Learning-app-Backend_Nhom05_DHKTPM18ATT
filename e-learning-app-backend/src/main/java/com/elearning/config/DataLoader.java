package com.elearning.config;

import com.elearning.entity.User;
import com.elearning.enums.UserRole;
import com.elearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            log.info("--- ĐANG TẠO DỮ LIỆU USER TEST (ADMIN) ---");
            User admin = new User();
            admin.setUsername("admin01");
            admin.setFullName("Admin Hệ Thống");
            admin.setEmail("admin@gmail.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.admin);
            admin.setAvatarUrl("https://via.placeholder.com/50");
            userRepository.save(admin);
            log.info(">>> Đã tạo user ADMIN. Email: admin@gmail.com, Password: admin123");
        }

        if (!userRepository.existsByEmail("a@gmail.com")) {
            log.info("--- ĐANG TẠO DỮ LIỆU USER TEST (STUDENT) ---");
            User student = new User();
            student.setUsername("student01");
            student.setFullName("Nguyễn Văn A");
            student.setEmail("a@gmail.com");
            student.setPasswordHash(passwordEncoder.encode("student123"));
            student.setRole(UserRole.student);
            student.setAvatarUrl("https://res.cloudinary.com/dwzjxsdli/image/upload/v1761704109/avatar1_k4i2wd.jpg");
            userRepository.save(student);
            log.info(">>> Đã tạo user STUDENT. Email: a@gmail.com, Password: student123");
        }

        if (!userRepository.existsByEmail("b@gmail.com")) {
            log.info("--- ĐANG TẠO DỮ LIỆU USER TEST (INSTRUCTOR) ---");
            User instructor = new User();
            instructor.setUsername("teacher01");
            instructor.setFullName("Trần Thị B");
            instructor.setEmail("b@gmail.com");
            instructor.setPasswordHash(passwordEncoder.encode("teacher123"));
            instructor.setRole(UserRole.instructor);
            instructor.setAvatarUrl("https://images.unsplash.com/photo-1544005313-94ddf0286df2");
            userRepository.save(instructor);
            log.info(">>> Đã tạo user INSTRUCTOR. Email: b@gmail.com, Password: teacher123");
        }
    }
}
package com.elearning.controller.student;

import com.elearning.modal.dto.response.UserResponseDTO;
import com.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("studentUserController")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/topTeachers")
    public ResponseEntity<?> getTopTeachers() {
        List<UserResponseDTO> topTeachers = userService.getTopInstructors();
        if (topTeachers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Không có giảng viên nổi bật nào.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(topTeachers);
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Integer id) {
        UserResponseDTO teacher = userService.getTeacherById(id);
        if (teacher == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Giảng viên không tồn tại.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(teacher);
    }
}

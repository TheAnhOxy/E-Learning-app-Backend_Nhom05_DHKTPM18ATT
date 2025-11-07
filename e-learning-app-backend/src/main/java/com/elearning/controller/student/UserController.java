package com.elearning.controller.student;

import com.elearning.entity.User;
import com.elearning.modal.dto.request.RegisterRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.response.UserResponseDTO;
import com.elearning.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            User user = userService.registerStudent(request);
            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Đăng ký thành công!")
                    .data(user.getId())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

}

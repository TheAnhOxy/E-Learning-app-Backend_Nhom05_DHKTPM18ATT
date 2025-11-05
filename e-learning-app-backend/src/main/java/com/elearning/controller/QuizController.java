package com.elearning.controller;

import com.elearning.modal.dto.request.QuizRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/quizzes") 
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<ApiResponse> createQuiz(@Valid @RequestBody QuizRequestDTO quizRequestDTO) {
        log.info("Nhận yêu cầu tạo quiz mới: {}", quizRequestDTO.getTitle());
        var quizData = quizService.createQuiz(quizRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo quiz thành công!")
                .data(quizData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getQuizById(@PathVariable Integer id) {
        log.info("Nhận yêu cầu lấy chi tiết quiz ID: {}", id);
        var quizData = quizService.getQuizById(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết quiz thành công")
                .data(quizData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateQuiz(
            @PathVariable Integer id,
            @Valid @RequestBody QuizRequestDTO quizRequestDTO
    ) {
        log.info("Nhận yêu cầu cập nhật quiz ID: {}", id);
        var updatedQuizData = quizService.updateQuiz(id, quizRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật quiz thành công")
                .data(updatedQuizData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteQuiz(@PathVariable Integer id) {
        log.info("Nhận yêu cầu xóa quiz ID: {}", id);
        quizService.deleteQuiz(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa quiz thành công")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
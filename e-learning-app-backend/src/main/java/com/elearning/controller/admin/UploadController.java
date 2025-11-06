package com.elearning.controller.admin;

import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final UploadService uploadService;

    /**
     * Endpoint để tải video lên Cloudinary.
     * Nhận file từ 'multipart/form-data' với key là 'file'.
     */
    @PostMapping("/video")
    public ResponseEntity<ApiResponse> uploadVideo(
            @RequestParam("file") MultipartFile file) {

        log.info("Nhận yêu cầu tải lên video: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("File rỗng, không thể tải lên.")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Gọi service của bạn để upload
            String videoUrl = uploadService.uploadVideo(file);

            // Trả về URL trong field 'data' để client có thể sử dụng
            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Tải video lên thành công")
                    .data(Map.of("url", videoUrl))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            log.error("Lỗi khi tải video lên Cloudinary: {}", e.getMessage());
            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi máy chủ khi tải file: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
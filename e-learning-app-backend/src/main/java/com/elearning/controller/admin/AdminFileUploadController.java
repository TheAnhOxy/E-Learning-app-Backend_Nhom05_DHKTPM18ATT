package com.elearning.controller.admin;

import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.modal.dto.response.FileUploadResponseDTO;
import com.elearning.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/files")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public class AdminFileUploadController {

    private final FileStorageService fileStorageService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        log.info("Nhận yêu cầu upload file: {}", file.getOriginalFilename());

        try {
            String fileUrl = fileStorageService.uploadFile(file);

            FileUploadResponseDTO fileResponse = new FileUploadResponseDTO(
                    fileUrl,
                    file.getContentType(),
                    file.getSize()
            );

            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Upload file thành công!")
                    .data(fileResponse)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IOException e) {
            log.error("Lỗi khi upload file: {}", e.getMessage());
            ApiResponse response = ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Upload file thất bại: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
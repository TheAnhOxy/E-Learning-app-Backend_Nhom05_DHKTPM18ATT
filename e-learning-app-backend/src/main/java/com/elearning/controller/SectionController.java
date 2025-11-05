package com.elearning.controller;


import com.elearning.modal.dto.request.SectionRequestDTO;
import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sections")
@RequiredArgsConstructor
@Slf4j
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<ApiResponse> createSection(@Valid @RequestBody SectionRequestDTO sectionRequestDTO) {
        log.info("Nhận yêu cầu tạo chương mới: {}", sectionRequestDTO.getTitle());
        var sectionData = sectionService.createSection(sectionRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo chương thành công!")
                .data(sectionData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSectionById(@PathVariable Integer id) {
        log.info("Nhận yêu cầu lấy chi tiết chương ID: {}", id);
        var sectionData = sectionService.getSectionById(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy chi tiết chương thành công")
                .data(sectionData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSection(
            @PathVariable Integer id,
            @Valid @RequestBody SectionRequestDTO sectionRequestDTO
    ) {
        log.info("Nhận yêu cầu cập nhật chương ID: {}", id);
        var updatedSectionData = sectionService.updateSection(id, sectionRequestDTO);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật chương thành công")
                .data(updatedSectionData)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSection(@PathVariable Integer id) {
        log.info("Nhận yêu cầu xóa chương ID: {}", id);
        sectionService.deleteSection(id);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Xóa chương thành công")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
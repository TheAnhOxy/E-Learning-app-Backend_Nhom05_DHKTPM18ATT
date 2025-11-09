package com.elearning.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.elearning.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File không hợp lệ hoặc rỗng.");
        }

        log.info("Bắt đầu upload file: {}", file.getOriginalFilename());
        Map<String, Object> uploadOptions = ObjectUtils.asMap(
                "resource_type", "auto", // Tự động nhận diện (image, video)
                "folder", "elearning" // (Tùy chọn) Lưu vào thư mục 'elearning' trên Cloudinary
                // "public_id", "custom_name" // (Tùy chọn) Tự đặt tên file
        );

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        String secureUrl = (String) uploadResult.get("secure_url");
        log.info("Upload thành công. URL: {}", secureUrl);

        return secureUrl;
    }
}
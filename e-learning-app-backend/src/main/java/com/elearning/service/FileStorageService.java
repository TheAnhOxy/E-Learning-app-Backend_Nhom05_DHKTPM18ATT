package com.elearning.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {

    /**
     * Tải file lên cloud và trả về URL
     * @param file File được gửi từ client
     * @return URL an toàn (https) của file đã upload
     * @throws IOException Nếu có lỗi trong quá trình upload
     */
    String uploadFile(MultipartFile file) throws IOException;

}
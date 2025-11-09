package com.elearning.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {

    String uploadVideo(MultipartFile file) throws IOException;

}

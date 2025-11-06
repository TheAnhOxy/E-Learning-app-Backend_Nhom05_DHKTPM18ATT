package com.elearning.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.elearning.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UploadServiceImpl implements UploadService {

    private final Cloudinary cloudinary;

    public UploadServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "video"));
        return uploadResult.get("secure_url").toString();
    }
}

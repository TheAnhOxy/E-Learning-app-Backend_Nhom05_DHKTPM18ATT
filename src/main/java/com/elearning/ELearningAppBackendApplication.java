package com.elearning;

import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//(exclude = { SecurityAutoConfiguration.class })
@SpringBootApplication
@MultipartConfig(
        fileSizeThreshold = 2 * 1024 * 1024, // 2MB
        maxFileSize = 10 * 1024 * 1024,      // 10MB
        maxRequestSize = 15 * 1024 * 1024    // 15MB
)
public class ELearningAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ELearningAppBackendApplication.class, args);
    }

}

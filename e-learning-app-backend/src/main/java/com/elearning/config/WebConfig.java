package com.elearning.config; // (Đảm bảo đúng package của bạn)

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // Áp dụng cho TẤT CẢ các API (vd: /admin/**, /api/v1/**)
//
//                // --- SỬA Ở ĐÂY ---
//                // Dùng .allowedOriginPatterns(...) thay vì .allowedOrigins(...)
//                // để hỗ trợ nhiều origins khi allowCredentials(true)
//                .allowedOriginPatterns(
//                        "http://localhost:8081", // Giao diện Dashboard (Expo) của bạn
//                        "http://localhost:3000"  // Giao diện Student (Dự phòng)
//                )
//
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
// WebConfig.java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
}
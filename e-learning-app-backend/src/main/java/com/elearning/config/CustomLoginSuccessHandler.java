package com.elearning.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final String STUDENT_URL = "http://localhost:8081/"; // Giao diện Student
    private final String ADMIN_DASHBOARD_URL = "http://localhost:3000/admin/dashboard"; // Dashboard
    private final String INSTRUCTOR_DASHBOARD_URL = "http://localhost:3000/admin/courses"; // Dashboard

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            log.warn("Response đã được committed. Không thể redirect đến {}", targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSTRUCTOR"));
        boolean isStudent = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT"));

        if (isAdmin) {
            log.info("Redirecting Admin to Dashboard (Port 3000)");
            return ADMIN_DASHBOARD_URL;
        } else if (isInstructor) {
            log.info("Redirecting Instructor to Dashboard (Port 3000)");
            return INSTRUCTOR_DASHBOARD_URL;
        } else if (isStudent) {
            log.info("Redirecting Student to Main Site (Port 8081)");
            return STUDENT_URL;
        } else {
            log.warn("User có vai trò không xác định, chuyển về trang chủ.");
            return STUDENT_URL;
        }
    }
}
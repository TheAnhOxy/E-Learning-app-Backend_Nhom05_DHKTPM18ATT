package com.elearning.config;

import com.elearning.service.impl.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(daoAuthenticationProvider());
        return builder.build();
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .requestMatchers(HttpMethod.GET,
//                                "/categories/**",
//                                "/courses", // Lấy ds khóa học
//                                "/courses/popular", // Lấy top khóa học
//                                "/courses/recommended",
//                                "/courses/inspiring",
//                                "/courses/{id}", // Xem chi tiết 1 khóa học
//                                "/reviews/course/{courseId}", // Xem review của khóa học
//                                "/users/topTeachers",
//                                "/lessons/preview/{id}"
//                        ).permitAll()
//                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
//                        .requestMatchers(
//                                "/orders/**",       // Tạo và xem đơn hàng
//                                "/reviews/**",      // Viết, sửa, xóa review của mình
//                                "/progress/**",     // Cập nhật tiến độ
//                                "/enrollments/**",  // Lấy khóa học của tôi
//                                "/my-courses/**",
//                                "/favorites/**"
//                        ).hasRole("STUDENT")
//
//                        .requestMatchers("/admin/statistics/**").hasAnyRole("INSTRUCTOR", "ADMIN")
//                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "INSTRUCTOR")
//
//                        .requestMatchers("/admin/users/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .usernameParameter("usernameOrEmail")
//                        .passwordParameter("password")
//                        .loginProcessingUrl("/login-process")
//                        .successHandler(myAuthenticationSuccessHandler())
//                        .failureUrl("/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login?logout=true")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )
//                .exceptionHandling(ex -> ex
//                        .accessDeniedPage("/access-denied")
//                );
//
//        return http.build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth
                    // === TẠM THỜI MỞ TẤT CẢ API ĐỂ TEST ===
                    .requestMatchers("/**").permitAll()
            ).formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // Xử lý lỗi 401 (chưa đăng nhập)
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            );

    // (Toàn bộ phần .formLogin() và .logout() không cần thiết
    // khi đã .permitAll() ở trên, nhưng để đó cũng không sao)

    return http.build();
}
}
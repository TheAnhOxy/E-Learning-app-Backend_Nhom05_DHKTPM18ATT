package com.elearning.service;


import com.elearning.entity.User;
import com.elearning.modal.dto.request.NotificationRequestDTO;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.response.StudentDetailResponseDTO;
import com.elearning.modal.dto.response.StudentResponseDTO;
import com.elearning.modal.dto.search.StudentSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User getUserEntityById(Integer id);
    Page<StudentResponseDTO> searchStudents(StudentSearchRequest request, Pageable pageable, CustomUserDetails currentUser);


    StudentDetailResponseDTO getStudentDetails(Integer userId, CustomUserDetails currentUser);


    StudentResponseDTO updateStudent(Integer userId, StudentUpdateRequestDTO dto, CustomUserDetails currentUser);

    void deleteStudent(Integer userId, CustomUserDetails currentUser);

    void sendNotificationToStudent(Integer userId, NotificationRequestDTO dto, CustomUserDetails currentUser);
}
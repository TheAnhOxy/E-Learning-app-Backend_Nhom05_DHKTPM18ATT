package com.elearning.service;


import com.elearning.entity.User;
import com.elearning.modal.dto.request.NotificationRequestDTO;
import com.elearning.modal.dto.request.StudentUpdateRequestDTO;
import com.elearning.modal.dto.response.StudentDetailResponseDTO;
import com.elearning.modal.dto.response.StudentResponseDTO;
import com.elearning.modal.dto.response.UserResponseDTO;
import com.elearning.modal.dto.search.StudentSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();
    User getUserEntityById(Integer id);
    Page<StudentResponseDTO> searchStudents(StudentSearchRequest request, Pageable pageable);
    StudentDetailResponseDTO getStudentDetails(Integer userId);
    StudentResponseDTO updateStudent(Integer userId, StudentUpdateRequestDTO dto);
    void deleteStudent(Integer userId);
    void sendNotificationToStudent(Integer userId, NotificationRequestDTO dto);

}
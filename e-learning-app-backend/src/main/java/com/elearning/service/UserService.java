package com.elearning.service;


import com.elearning.entity.User;
import com.elearning.modal.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {


    List<UserResponseDTO> getAllUsers();


    User getUserEntityById(Integer id);
}
package com.elearning.converter;

import com.elearning.entity.User;
import com.elearning.modal.dto.response.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final ModelMapper modelMapper;

    public UserResponseDTO toDTO(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

}
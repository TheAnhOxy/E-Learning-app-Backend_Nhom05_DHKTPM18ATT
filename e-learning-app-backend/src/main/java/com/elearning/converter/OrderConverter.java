package com.elearning.converter;

import com.elearning.entity.Order;
import com.elearning.modal.dto.response.OrderDetailResponseDTO;
import com.elearning.modal.dto.response.OrderResponseDTO;
import com.elearning.modal.dto.response.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderConverter {

    private final ModelMapper modelMapper;


    public OrderResponseDTO toDTO(Order entity) {
        OrderResponseDTO dto = modelMapper.map(entity, OrderResponseDTO.class);

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
            dto.setUserEmail(entity.getUser().getEmail());
        }

        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
            dto.setCourseTitle(entity.getCourse().getTitle());
        }

        return dto;
    }


    public OrderDetailResponseDTO toDetailDTO(Order entity, List<TransactionResponseDTO> transactionHistory) {
        OrderDetailResponseDTO dto = modelMapper.map(entity, OrderDetailResponseDTO.class);
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
        }
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
            dto.setCourseTitle(entity.getCourse().getTitle());
            dto.setCourseThumbnailUrl(entity.getCourse().getThumbnailUrl());
        }
        dto.setTransactions(transactionHistory);

        return dto;
    }
}
package com.elearning.converter;


import com.elearning.entity.Transaction;
import com.elearning.modal.dto.response.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionConverter {

    private final ModelMapper modelMapper;

    public TransactionResponseDTO toDTO(Transaction entity) {
        TransactionResponseDTO dto = modelMapper.map(entity, TransactionResponseDTO.class);
        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }
        if (entity.getOrder() != null && entity.getOrder().getUser() != null) {
            dto.setUserFullName(entity.getOrder().getUser().getFullName());
            dto.setUserAvatarUrl(entity.getOrder().getUser().getAvatarUrl());
        }

        if (entity.getOrder() != null && entity.getOrder().getCourse() != null) {
            dto.setCourseId(entity.getOrder().getCourse().getId());
            dto.setCourseTitle(entity.getOrder().getCourse().getTitle());
        }
        return dto;
    }


}
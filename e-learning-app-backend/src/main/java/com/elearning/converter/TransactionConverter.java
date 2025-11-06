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
        return dto;
    }
}
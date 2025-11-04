package com.elearning.converter;

import com.pet.config.ModelMapperConfig;
import com.pet.entity.Category;
import com.pet.modal.response.CategoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {
    @Autowired
    private ModelMapperConfig modelMapper;

    public CategoryResponseDTO mapToDTO(Category category) {
        return modelMapper.getModelMapper().map(category, CategoryResponseDTO.class);
    }
}

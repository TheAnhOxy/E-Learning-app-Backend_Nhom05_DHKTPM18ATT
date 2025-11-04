package com.elearning.converter;

import com.pet.config.ModelMapperConfig;
import com.pet.entity.Category;
import com.pet.entity.Promotion;
import com.pet.exception.ResourceNotFoundException;
import com.pet.modal.request.PromotionRequestDTO;
import com.pet.modal.response.PromotionResponseDTO;
import com.pet.repository.CategoryRepository;
import com.pet.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromotionConverter {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapperConfig modelMapper;

    public PromotionResponseDTO mapToDTO(Promotion promotion) {
        PromotionResponseDTO responseDTO = modelMapper.getModelMapper().map(promotion, PromotionResponseDTO.class);
        responseDTO.setCategoryId(promotion.getCategory() != null ? promotion.getCategory().getCategoryId() : null);
        return responseDTO;
    }

    public Promotion mapToEntity(PromotionRequestDTO requestDTO, Promotion promotion) {
        promotion.setCategory(null); // Đặt category thành null trước khi ánh xạ để tránh ghi đè không mong muốn
        modelMapper.getModelMapper().map(requestDTO, promotion);

        if(promotion.getPromotionId() == null){
            promotion.setPromotionId(generatePromotionId()); //Hoặc UUID.randomUUID().toString()
        }
        if(requestDTO.getCategoryId() != null){
            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));
            promotion.setCategory(category);
        } else {
            promotion.setCategory(null);
        }
        return promotion;
    }

    private String generatePromotionId() {
        long count = promotionRepository.count() + 1;
        return String.format("PR%03d", count);
    }
}

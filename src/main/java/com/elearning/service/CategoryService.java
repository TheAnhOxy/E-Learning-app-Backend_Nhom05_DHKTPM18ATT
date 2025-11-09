package com.elearning.service;

import com.elearning.modal.dto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> getAll();
}
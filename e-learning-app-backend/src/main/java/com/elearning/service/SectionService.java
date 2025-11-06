package com.elearning.service;

import com.elearning.modal.dto.request.SectionRequestDTO;
import com.elearning.modal.dto.response.SectionResponseDTO;

import java.util.List;

public interface SectionService {
    SectionResponseDTO createSection(SectionRequestDTO sectionRequestDTO);
    SectionResponseDTO updateSection(Integer id, SectionRequestDTO sectionRequestDTO);
    void deleteSection(Integer id);
    SectionResponseDTO getSectionById(Integer id);
    List<SectionResponseDTO> getSectionsByCourseId(Integer courseId);
}
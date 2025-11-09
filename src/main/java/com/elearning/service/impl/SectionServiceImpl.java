package com.elearning.service.impl;

import com.elearning.converter.SectionConverter;

import com.elearning.entity.Section;
import com.elearning.modal.dto.request.SectionRequestDTO;
import com.elearning.modal.dto.response.LessonResponseDTO;
import com.elearning.modal.dto.response.SectionResponseDTO;
import com.elearning.repository.SectionRepository;
import com.elearning.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionConverter sectionConverter;

    @Override
    @Transactional
    public SectionResponseDTO createSection(SectionRequestDTO sectionRequestDTO) {
        log.info("Bắt đầu tạo chương (section)...");
        Section section = sectionConverter.toEntity(sectionRequestDTO);
        Section savedSection = sectionRepository.save(section);
        log.info("Đã tạo chương thành công với ID: {}", savedSection.getId());
        return sectionConverter.toDTO(savedSection);
    }

    @Override
    public List<SectionResponseDTO> getSectionsByCourseId(Integer courseId) {
        List<Section> sections = sectionRepository.findByCourseId(courseId);

        return sections.stream()
                .map(section -> SectionResponseDTO.builder()
                        .id(section.getId())
                        .title(section.getTitle())
                        .orderIndex(section.getOrderIndex())
                        .courseId(section.getCourse().getId())
                        .lessons(section.getLessons().stream()
                                .map(lesson -> LessonResponseDTO.builder()
                                        .id(lesson.getId())
                                        .title(lesson.getTitle())
                                        .provider(lesson.getProvider())
                                        .playbackUrl(lesson.getPlaybackUrl())
                                        .videoDescription(lesson.getVideoDescription())
                                        .durationInSeconds(lesson.getDurationInSeconds())
                                        .sectionId(section.getId())
                                        .viewsCount(lesson.getViewsCount())
                                        .isFree(lesson.getIsFree())
                                        .orderIndex(lesson.getOrderIndex())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }


    @Override
    @Transactional
    public SectionResponseDTO updateSection(Integer id, SectionRequestDTO sectionRequestDTO) {
        log.info("Bắt đầu cập nhật chương ID: {}", id);
        Section existingSection = sectionRepository.findById(id)
                .orElseThrow();
        sectionConverter.updateEntity(existingSection, sectionRequestDTO);
        Section updatedSection = sectionRepository.save(existingSection);
        log.info("Đã cập nhật chương thành công.");
        return sectionConverter.toDTO(updatedSection);
    }

    @Override
    @Transactional
    public void deleteSection(Integer id) {
        log.info("Bắt đầu xóa chương ID: {}", id);
        Section section = sectionRepository.findById(id)
                .orElseThrow();
        sectionRepository.delete(section);
        log.info("Đã xóa chương thành công.");
    }

    @Override
    @Transactional(readOnly = true)
    public SectionResponseDTO getSectionById(Integer id) {
        log.info("Đang lấy chi tiết chương ID: {}", id);
        Section section = sectionRepository.findById(id)
                .orElseThrow();
        return sectionConverter.toDTO(section);
    }

    // ------------------ Additional Methods ----------------//
    @Override
    public List<SectionResponseDTO> getSectionsByCourse(Integer courseId) {
        log.info("Đang lấy danh sách chương cho khóa học ID: {}", courseId);
        List<Section> sections = sectionRepository.findByCourse_Id(courseId);
        log.info("Đã lấy {} chương.", sections.size());
        return sections.stream()
                .map(sectionConverter::toDTO)
                .toList();
    }
}
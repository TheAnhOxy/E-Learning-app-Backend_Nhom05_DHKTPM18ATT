package com.elearning.service.impl;

import com.elearning.converter.UserConverter;
import com.elearning.entity.User;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.response.UserResponseDTO;
import com.elearning.repository.UserRepository;
import com.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.info("Đang lấy tất cả user từ repository...");

        List<User> users = userRepository.findAll(Sort.by("id").ascending());
        log.info("Đã lấy {} users, đang chuyển đổi sang DTO...", users.size());
        return users.stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserEntityById(Integer id) {
        log.debug("Đang tìm User entity với ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow();
    }
}
package com.elearning.controller;


import com.elearning.modal.dto.response.ApiResponse;
import com.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @GetMapping("/search")
    public ResponseEntity<ApiResponse> testGetAllUsers() {
        log.info(" Lấy tất cả người dùng...");
        var usersData = userService.getAllUsers();
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Lấy " + usersData.size() + " users thành công.")
                .data(usersData)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
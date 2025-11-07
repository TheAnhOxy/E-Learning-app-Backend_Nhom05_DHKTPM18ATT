package com.elearning.modal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Integer id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Integer courseId;
    private Integer userId;
    private String userFullName;
    private String userAvatarUrl;

//    private String replyText;
//    private LocalDateTime repliedAt;
//    private String repliedByFullName;
//    private String repliedByAvatarUrl;

}
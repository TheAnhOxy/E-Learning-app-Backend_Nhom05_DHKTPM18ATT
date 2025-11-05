package com.elearning.service;

import com.elearning.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationService {
    @Transactional
    void sendNotification(User user, String title, String message);
}

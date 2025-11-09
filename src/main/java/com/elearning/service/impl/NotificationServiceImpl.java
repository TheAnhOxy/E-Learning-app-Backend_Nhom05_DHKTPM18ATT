package com.elearning.service.impl;
import com.elearning.entity.Notification;
import com.elearning.entity.User;
import com.elearning.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements com.elearning.service.NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public void sendNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationRepository.save(notification);
        log.info("Đã lưu thông báo (ID: {}) cho User ID: {}", notification.getId(), user.getId());
        log.warn("--- GIẢ LẬP GỬI EMAIL ---");
        log.warn("Đến: {}", user.getEmail());
        log.warn("Tiêu đề: {}", title);
        log.warn("Nội dung: {}", message);
        log.warn("--- KẾT THÚC GIẢ LẬP ---");

    }
}

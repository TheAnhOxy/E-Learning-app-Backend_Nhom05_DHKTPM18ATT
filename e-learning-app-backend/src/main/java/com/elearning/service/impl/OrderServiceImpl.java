package com.elearning.service.impl;


import com.elearning.converter.OrderConverter;
import com.elearning.converter.TransactionConverter;

import com.elearning.entity.Course;
import com.elearning.entity.Enrollment;
import com.elearning.entity.Order;
import com.elearning.entity.Transaction;
import com.elearning.entity.User;
import com.elearning.enums.OrderStatus;
import com.elearning.enums.TransactionStatus;
import com.elearning.exception.ConflictException;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.CreateOrderRequestDTO;
import com.elearning.modal.dto.response.CreateOrderResponseDTO;
import com.elearning.modal.dto.response.OrderDetailResponseDTO;
import com.elearning.modal.dto.response.OrderResponseDTO;
import com.elearning.modal.dto.response.TransactionResponseDTO;
import com.elearning.modal.dto.search.OrderSearchRequest;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.OrderRepository;
import com.elearning.repository.TransactionRepository;
import com.elearning.service.OrderService;
import com.elearning.service.PaymentService;
import com.elearning.service.UserService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final OrderConverter orderConverter;
    private final TransactionConverter transactionConverter;

    @Override
    @Transactional
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO dto, Integer userId) {
        log.info("Student ID {} tạo đơn hàng cho Khóa học ID {}", userId, dto.getCourseId());

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, dto.getCourseId())) {
            throw new ConflictException("Bạn đã sở hữu khóa học này.");
        }
        User user = userService.getUserEntityById(userId);
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Order order = new Order();
        order.setUser(user);
        order.setCourse(course);
        order.setAmount(course.getPrice());
        if (course.getPrice() == null || course.getPrice() <= 0) {
            log.info("Khóa học miễn phí. Tự động hoàn thành đơn hàng và ghi danh.");
            order.setStatus(OrderStatus.completed);
            Order savedOrder = orderRepository.save(order);
            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setOrder(savedOrder);
            enrollment.setEnrollmentDate(LocalDateTime.now());
            enrollmentRepository.save(enrollment);


            return CreateOrderResponseDTO.builder()
                    .orderId(savedOrder.getId())
                    .transactionId(null)
                    .amount(0)
                    .orderStatus(OrderStatus.completed)
                    .qrCodeData(null)
                    .build();
        }

        log.info("Khóa học có phí ({}). Đang tạo giao dịch...", course.getPrice());
        order.setStatus(OrderStatus.pending);
        Order savedOrder = orderRepository.save(order);

        Transaction transaction = new Transaction();
        transaction.setOrder(savedOrder);
        transaction.setAmount(savedOrder.getAmount());
        transaction.setStatus(TransactionStatus.pending);
        transaction.setPaymentMethod(dto.getPaymentMethod());
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Đã tạo Order ID {} và Transaction ID {} (pending)", savedOrder.getId(), savedTransaction.getId());

        // Gọi Payment Service để tạo QR Code
        return paymentService.createPaymentRequest(savedTransaction);
    }

    @Override
    @Transactional
    public void markOrderAsCompleted(Integer orderId) {
        log.info("Admin đánh dấu hoàn thành cho Order ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (order.getStatus() == OrderStatus.completed) {
            log.warn("Đơn hàng ID {} đã hoàn thành rồi.", orderId);
            return;
        }

        order.setStatus(OrderStatus.completed);
        orderRepository.save(order);


        if (!enrollmentRepository.existsByUserIdAndCourseId(order.getUser().getId(), order.getCourse().getId())) {
            createEnrollment(order.getUser(), order.getCourse(), order);
        } else {
            log.warn("Học viên ID {} đã được ghi danh vào khóa học ID {} từ trước.", order.getUser().getId(), order.getCourse().getId());
        }
    }

    private void createEnrollment(User user, Course course, Order order) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setOrder(order);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
        log.info("Đã tạo ghi danh cho User ID {} vào Khóa học ID {}", user.getId(), course.getId());
    }

    @Override
    @Transactional
    public void deleteOrder(Integer orderId) {
        log.info("Admin xóa Order ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

         if(order.getStatus() == OrderStatus.completed) {
             throw new ConflictException("Không thể xóa đơn hàng đã hoàn thành.");
         }
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getMyOrderDetails(Integer orderId, Integer userId) {
        log.info("Student ID {} xem chi tiết đơn hàng ID {}", userId, orderId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return getOrderDetails(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getOrderDetailsById(Integer orderId) {
        log.info("Admin/Teacher xem chi tiết đơn hàng ID {}", orderId);
        // Dùng custom query để fetch User và Course
        Order order = orderRepository.findOrderByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return getOrderDetails(order);
    }

    private OrderDetailResponseDTO getOrderDetails(Order order) {
        List<Transaction> transactions = transactionRepository.findAllByOrderIdOrderByCreatedAtDesc(order.getId());
        List<TransactionResponseDTO> transactionHistory = transactions.stream()
                .map(transactionConverter::toDTO)
                .collect(Collectors.toList());
        User user = order.getUser();
        Course course = order.getCourse();

        // Xây dựng DTO chi tiết
        return OrderDetailResponseDTO.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .userId(user.getId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .userAvatarUrl(user.getAvatarUrl())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseThumbnailUrl(course.getThumbnailUrl())
                .transactions(transactionHistory)
                .build();
    }

//    private OrderDetailResponseDTO getOrderDetails(Order order) {
//        List<Transaction> transactions = transactionRepository.findAllByOrderIdOrderByCreatedAtDesc(order.getId());
//        List<TransactionResponseDTO> transactionHistory = transactions.stream()
//                .map(transactionConverter::toDTO)
//                .collect(Collectors.toList());
//
//        return orderConverter.toDetailDTO(order, transactionHistory);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> searchOrders(OrderSearchRequest searchRequest, Pageable pageable) {
        log.info("Admin tìm kiếm đơn hàng với query: {}, status: {}, userId: {}, courseId: {}",
                searchRequest.getQuery(), searchRequest.getStatus(), searchRequest.getUserId(), searchRequest.getCourseId());
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Status (nếu có)
            if (searchRequest.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), searchRequest.getStatus()));
            }

            // 2. Lọc theo User ID (nếu có)
            if (searchRequest.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), searchRequest.getUserId()));
            }

            // 3. Lọc theo Course ID (nếu có)
            if (searchRequest.getCourseId() != null) {
                predicates.add(cb.equal(root.get("course").get("id"), searchRequest.getCourseId()));
            }
            if (StringUtils.hasText(searchRequest.getQuery())) {
                String queryLower = "%" + searchRequest.getQuery().toLowerCase() + "%";

                Predicate byUserName = cb.like(cb.lower(root.join("user").get("fullName")), queryLower);
                Predicate byCourseName = cb.like(cb.lower(root.join("course").get("title")), queryLower);

                Predicate byOrderId = cb.conjunction();
                try {
                    Integer queryId = Integer.parseInt(searchRequest.getQuery());
                    byOrderId = cb.equal(root.get("id"), queryId);
                } catch (NumberFormatException e) {
                }

                predicates.add(cb.or(byUserName, byCourseName, byOrderId));
            }
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("user", JoinType.LEFT);
                root.fetch("course", JoinType.LEFT);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        return orderPage.map(orderConverter::toDTO);
    }
}
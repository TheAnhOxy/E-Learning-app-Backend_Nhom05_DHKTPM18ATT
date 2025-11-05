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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return getOrderDetails(order);
    }

    private OrderDetailResponseDTO getOrderDetails(Order order) {
        List<Transaction> transactions = transactionRepository.findAllByOrderIdOrderByCreatedAtDesc(order.getId());
        List<TransactionResponseDTO> transactionHistory = transactions.stream()
                .map(transactionConverter::toDTO)
                .collect(Collectors.toList());

        return orderConverter.toDetailDTO(order, transactionHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> searchOrders(OrderSearchRequest searchRequest, Pageable pageable) {
        log.info("Admin tìm kiếm đơn hàng...");

        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        return orderPage.map(orderConverter::toDTO);
    }
}
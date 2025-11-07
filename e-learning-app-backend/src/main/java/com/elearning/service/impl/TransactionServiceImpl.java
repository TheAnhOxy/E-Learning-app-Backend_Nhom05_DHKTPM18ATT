package com.elearning.service.impl;

import com.elearning.converter.TransactionConverter;
import com.elearning.entity.Enrollment;
import com.elearning.entity.Order;
import com.elearning.entity.Transaction;
import com.elearning.enums.OrderStatus;
import com.elearning.enums.TransactionStatus;
import com.elearning.exception.ConflictException;
import com.elearning.exception.ResourceNotFoundException;
import com.elearning.modal.dto.request.RefundRequestDTO;
import com.elearning.modal.dto.response.TransactionResponseDTO;
import com.elearning.modal.dto.search.TransactionSearchRequest;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.OrderRepository;
import com.elearning.repository.TransactionRepository;
import com.elearning.service.PaymentService;
import com.elearning.service.TransactionService;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentService paymentService;
    private final TransactionConverter transactionConverter;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> searchTransactions(TransactionSearchRequest searchRequest, Pageable pageable) {
        log.info("Admin tìm kiếm giao dịch với: {}", searchRequest.toString());
        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            var orderJoin = root.join("order");
            if (searchRequest.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), searchRequest.getStatus()));
            }
            if (StringUtils.hasText(searchRequest.getPaymentMethod())) {
                predicates.add(cb.equal(root.get("paymentMethod"), searchRequest.getPaymentMethod()));
            }
            if (searchRequest.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchRequest.getFromDate().atStartOfDay()));
            }
            if (searchRequest.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchRequest.getToDate().atTime(23, 59, 59)));
            }
            if (searchRequest.getUserId() != null) {
                predicates.add(cb.equal(orderJoin.join("user").get("id"), searchRequest.getUserId()));
            }
            if (StringUtils.hasText(searchRequest.getQuery())) {
                String queryLower = "%" + searchRequest.getQuery().toLowerCase() + "%";

                Predicate byUserName = cb.like(cb.lower(orderJoin.join("user").get("fullName")), queryLower);
                Predicate byCourseName = cb.like(cb.lower(orderJoin.join("course").get("title")), queryLower);
                Predicate byTransactionCode = cb.like(cb.lower(root.get("transactionCode")), queryLower);

                Predicate byTransactionId = cb.conjunction();
                try {
                    Integer queryId = Integer.parseInt(searchRequest.getQuery());
                    byTransactionId = cb.equal(root.get("id"), queryId);
                } catch (NumberFormatException e) {
                }

                predicates.add(cb.or(byUserName, byCourseName, byTransactionCode, byTransactionId));
            }
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("order", JoinType.LEFT)
                        .fetch("user", JoinType.LEFT);
                root.fetch("order", JoinType.LEFT)
                        .fetch("course", JoinType.LEFT);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
        return transactionPage.map(transactionConverter::toDTO);
    }

    @Override
    @Transactional
    public TransactionResponseDTO createRefund(Integer originalTransactionId, RefundRequestDTO dto) {
        log.info("Admin yêu cầu hoàn tiền cho Transaction ID: {} với lý do: {}", originalTransactionId, dto.getReason());
        Transaction originalTransaction = transactionRepository.findById(originalTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (originalTransaction.getStatus() != TransactionStatus.success) {
            throw new ConflictException("Chỉ có thể hoàn tiền cho giao dịch đã THÀNH CÔNG.");
        }
        if (originalTransaction.getAmount() <= 0) {
            throw new ConflictException("Không thể hoàn tiền cho một giao dịch hoàn tiền hoặc giao dịch 0 đồng.");
        }

        Order order = originalTransaction.getOrder();
        if (order.getStatus() == OrderStatus.failed) {
            throw new ConflictException("Đơn hàng này đã ở trạng thái Thất bại (có thể đã được hoàn tiền).");
        }
        Transaction refundTransaction = paymentService.createRefund(originalTransaction, dto.getReason());
        Transaction savedRefundTransaction = transactionRepository.save(refundTransaction);

        order.setStatus(OrderStatus.failed);
        orderRepository.save(order);

        Enrollment enrollment = enrollmentRepository.findByOrderId(order.getId());
        if (enrollment != null) {
            enrollmentRepository.delete(enrollment);
            log.info("Đã xóa quyền truy cập (Enrollment ID: {}) của User ID: {} cho Khóa học ID: {}",
                    enrollment.getId(), order.getUser().getId(), order.getCourse().getId());
        }
        log.info("Đã hoàn tiền thành công. Giao dịch hoàn tiền mới ID: {}", savedRefundTransaction.getId());
        return transactionConverter.toDTO(savedRefundTransaction);
    }

}
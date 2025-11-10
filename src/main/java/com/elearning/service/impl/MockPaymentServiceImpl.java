package com.elearning.service.impl;

import com.elearning.entity.Order;
import com.elearning.entity.Transaction;
import com.elearning.enums.TransactionStatus;
import com.elearning.modal.dto.response.CreateOrderResponseDTO;
import com.elearning.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockPaymentServiceImpl implements PaymentService {

    @Override
    public CreateOrderResponseDTO createPaymentRequest(Transaction transaction) {
        log.info("Đang giả lập tạo yêu cầu thanh toán cho Transaction ID: {}", transaction.getId());
        Order order = transaction.getOrder();
        String mockPaymentUrl = String.format(
                "%s://pay?amount=%d&orderId=%s&txnId=%s",
                transaction.getPaymentMethod().toLowerCase(),
                order.getAmount(),
                order.getId(),
                transaction.getId()
        );

        log.info("Đã tạo Mock QR Code Data: {}", mockPaymentUrl);

        // Front-end sẽ dùng 'qrCodeData' ( URL) để render QR code
        return CreateOrderResponseDTO.builder()
                .orderId(order.getId())
                .transactionId(transaction.getId())
                .amount(order.getAmount())
                .orderStatus(order.getStatus())
                .qrCodeData(mockPaymentUrl) // data cho React Native render QR
                .qrCodeImageUrl(null)
                .build();
    }

    @Override
    public Transaction createRefund(Transaction originalTransaction, String reason) {
        log.info("Đang giả lập hoàn tiền cho Transaction ID: {} với lý do: {}", originalTransaction.getId(), reason);

        // (Nếu true: Gọi API của cổng thanh toán ở đây)
        Transaction refundTransaction = new Transaction();
        refundTransaction.setOrder(originalTransaction.getOrder());
        refundTransaction.setAmount(-originalTransaction.getAmount()); // Số tiền âm
        refundTransaction.setStatus(TransactionStatus.success);
        refundTransaction.setPaymentMethod("refund");
        refundTransaction.setTransactionCode("REFUND_" + originalTransaction.getId() + "_" + UUID.randomUUID().toString().substring(0, 8));

        log.info("Đã tạo giao dịch hoàn tiền mới, Transaction Code: {}", refundTransaction.getTransactionCode());

        return refundTransaction;
    }
}
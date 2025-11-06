package com.elearning.service;

import com.elearning.entity.Transaction;
import com.elearning.modal.dto.response.CreateOrderResponseDTO;

public interface PaymentService {

    CreateOrderResponseDTO createPaymentRequest(Transaction transaction);

    Transaction createRefund(Transaction originalTransaction, String reason);
}
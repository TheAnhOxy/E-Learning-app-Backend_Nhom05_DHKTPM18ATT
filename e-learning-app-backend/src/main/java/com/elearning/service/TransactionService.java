package com.elearning.service;

import com.elearning.modal.dto.request.RefundRequestDTO;
import com.elearning.modal.dto.response.TransactionResponseDTO;
import com.elearning.modal.dto.search.TransactionSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Page<TransactionResponseDTO> searchTransactions(TransactionSearchRequest searchRequest, Pageable pageable);

    TransactionResponseDTO createRefund(Integer originalTransactionId, RefundRequestDTO dto);
}
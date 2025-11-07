package com.elearning.repository;

import com.elearning.entity.Transaction;
import com.elearning.modal.dto.response.TimeSeriesDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findAllByOrderIdOrderByCreatedAtDesc(Integer orderId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'success'")
    BigDecimal findTotalRevenue();


    @Query("SELECT CAST(t.createdAt AS DATE), SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.status = 'success' AND t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(t.createdAt AS DATE) " +
            "ORDER BY CAST(t.createdAt AS DATE) ASC")
    List<Object[]> getRevenueStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Optional<Transaction> findByOrder_Id(Integer id);
}

package com.elearning.repository;

import com.elearning.entity.Transaction;
import com.elearning.modal.dto.response.TimeSeriesDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction> {

//    List<Transaction> findAllByOrderIdOrderByCreatedAtDesc(Integer orderId);

//    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'success'")
//    BigDecimal findTotalRevenue();


    @Query("SELECT CAST(t.createdAt AS DATE), SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.status = 'success' AND t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(t.createdAt AS DATE) " +
            "ORDER BY CAST(t.createdAt AS DATE) ASC")
    List<Object[]> getRevenueStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Optional<Transaction> findByOrder_Id(Integer id);
    @Query("SELECT t FROM Transaction t JOIN FETCH t.order o JOIN FETCH o.user JOIN FETCH o.course WHERE t.order.id = :orderId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByOrderIdOrderByCreatedAtDesc(Integer orderId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'success' AND t.amount > 0")
    BigDecimal findTotalRevenue();

    @Query("SELECT SUM(t.amount) " +
            "FROM Transaction t " +
            "JOIN t.order o " +
            "JOIN o.course c " +
            "WHERE t.status = 'success' AND t.amount > 0 AND c.instructor.id = :instructorId")
    BigDecimal findTotalRevenueByInstructor(@Param("instructorId") Integer instructorId);

    @Query(value = "SELECT CAST(t.created_at AS DATE) as transaction_date, SUM(t.amount) as daily_revenue " +
            "FROM transactions t " +
            "JOIN orders o ON t.order_id = o.id " +
            "JOIN courses c ON o.course_id = c.id " +
            "WHERE t.status = 'success' AND t.amount > 0 " +
            "AND c.instructor_id = :instructorId " +
            "AND t.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY transaction_date " +
            "ORDER BY transaction_date ASC", nativeQuery = true)
    List<Object[]> getRevenueStatsByDateRangeAndInstructor(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("instructorId") Integer instructorId
    );
}

package com.elearning.repository;

import com.elearning.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    @Query("SELECT c FROM Certificate c JOIN FETCH c.course co WHERE c.user.id = :userId ORDER BY c.issueDate DESC")
    List<Certificate> findAllWithCourseByUserId(Integer userId);
}
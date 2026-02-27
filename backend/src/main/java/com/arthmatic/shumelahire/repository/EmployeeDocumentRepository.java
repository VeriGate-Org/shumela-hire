package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.EmployeeDocument;
import com.arthmatic.shumelahire.entity.EmployeeDocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    List<EmployeeDocument> findByEmployeeIdAndDocumentType(Long employeeId, EmployeeDocumentType documentType);

    List<EmployeeDocument> findByEmployeeIdAndIsCurrentTrue(Long employeeId);

    Page<EmployeeDocument> findByEmployeeId(Long employeeId, Pageable pageable);

    // Find expiring documents
    @Query("SELECT d FROM EmployeeDocument d WHERE d.isCurrent = true AND d.expiryDate IS NOT NULL " +
           "AND d.expiryDate BETWEEN :now AND :threshold")
    List<EmployeeDocument> findExpiringSoon(@Param("now") LocalDate now, @Param("threshold") LocalDate threshold);

    // Find expired documents
    @Query("SELECT d FROM EmployeeDocument d WHERE d.isCurrent = true AND d.expiryDate IS NOT NULL " +
           "AND d.expiryDate < :now")
    List<EmployeeDocument> findExpired(@Param("now") LocalDate now);

    // Find current version of a document type for an employee
    @Query("SELECT d FROM EmployeeDocument d WHERE d.employeeId = :employeeId " +
           "AND d.documentType = :type AND d.isCurrent = true")
    List<EmployeeDocument> findCurrentByEmployeeAndType(
            @Param("employeeId") Long employeeId,
            @Param("type") EmployeeDocumentType type);

    // Count documents by employee
    long countByEmployeeId(Long employeeId);
}

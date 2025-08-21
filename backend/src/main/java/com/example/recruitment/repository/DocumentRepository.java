package com.example.recruitment.repository;

import com.example.recruitment.entity.Document;
import com.example.recruitment.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    // Find documents by applicant
    List<Document> findByApplicantIdOrderByUploadedAtDesc(Long applicantId);
    
    // Find documents by applicant and type
    List<Document> findByApplicantIdAndType(Long applicantId, DocumentType type);
    
    // Find documents by application
    List<Document> findByApplicationId(Long applicationId);
    
    // Find CV documents by applicant
    @Query("SELECT d FROM Document d WHERE d.applicant.id = :applicantId AND d.type = 'CV' ORDER BY d.uploadedAt DESC")
    List<Document> findCvDocumentsByApplicant(@Param("applicantId") Long applicantId);
    
    // Find supporting documents by applicant
    @Query("SELECT d FROM Document d WHERE d.applicant.id = :applicantId AND d.type = 'SUPPORT' ORDER BY d.uploadedAt DESC")
    List<Document> findSupportingDocumentsByApplicant(@Param("applicantId") Long applicantId);
    
    // Count documents by applicant
    long countByApplicantId(Long applicantId);
    
    // Count documents by type
    long countByType(DocumentType type);
    
    // Find documents by applicant and application
    List<Document> findByApplicantIdAndApplicationId(Long applicantId, Long applicationId);
    
    // Delete documents by applicant
    void deleteByApplicantId(Long applicantId);
}
package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.EmployeeDocumentResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeDocument;
import com.arthmatic.shumelahire.entity.EmployeeDocumentType;
import com.arthmatic.shumelahire.repository.EmployeeDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDocumentService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Autowired
    private EmployeeDocumentRepository documentRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AuditLogService auditLogService;

    public EmployeeDocumentResponse uploadDocument(Long employeeId, EmployeeDocumentType documentType,
                                                    String name, MultipartFile file,
                                                    LocalDate expiryDate, LocalDate issuedDate,
                                                    String issuingAuthority, String notes) throws IOException {
        logger.info("Uploading {} document for employee: {}", documentType, employeeId);

        validateFile(file);

        Employee employee = employeeService.findEmployeeById(employeeId);

        // Mark previous versions as not current
        List<EmployeeDocument> existingDocs = documentRepository.findCurrentByEmployeeAndType(employeeId, documentType);
        int nextVersion = 1;
        for (EmployeeDocument existing : existingDocs) {
            existing.setIsCurrent(false);
            documentRepository.save(existing);
            nextVersion = existing.getVersion() + 1;
        }

        // Store file
        String fileUrl = fileStorageService.store(file);

        // Create document record
        EmployeeDocument document = new EmployeeDocument();
        document.setEmployee(employee);
        document.setDocumentType(documentType);
        document.setName(name != null ? name : documentType.name());
        document.setFilename(file.getOriginalFilename());
        document.setFileUrl(fileUrl);
        document.setFileSize(file.getSize());
        document.setContentType(file.getContentType());
        document.setVersion(nextVersion);
        document.setIsCurrent(true);
        document.setExpiryDate(expiryDate);
        document.setIssuedDate(issuedDate);
        document.setIssuingAuthority(issuingAuthority);
        document.setNotes(notes);

        EmployeeDocument saved = documentRepository.save(document);

        auditLogService.logSystemAction("DOCUMENT_UPLOADED", "EMPLOYEE_DOCUMENT",
                "Document uploaded for employee " + employeeId + ": " + documentType + " v" + nextVersion);

        logger.info("Document uploaded with ID: {} (v{})", saved.getId(), saved.getVersion());

        return EmployeeDocumentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponse> getEmployeeDocuments(Long employeeId) {
        List<EmployeeDocument> documents = documentRepository.findByEmployeeIdAndIsCurrentTrue(employeeId);
        return documents.stream()
                .map(EmployeeDocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDocumentResponse> getEmployeeDocumentsPaged(Long employeeId, Pageable pageable) {
        return documentRepository.findByEmployeeId(employeeId, pageable)
                .map(EmployeeDocumentResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public EmployeeDocumentResponse getDocument(Long documentId) {
        EmployeeDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        return EmployeeDocumentResponse.fromEntity(document);
    }

    public void deleteDocument(Long employeeId, Long documentId) {
        logger.info("Deleting document {} for employee: {}", documentId, employeeId);

        EmployeeDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        if (!document.getEmployeeId().equals(employeeId)) {
            throw new IllegalArgumentException("Document does not belong to employee");
        }

        try {
            fileStorageService.delete(document.getFileUrl());
        } catch (Exception e) {
            logger.warn("Failed to delete file from storage: {}", document.getFileUrl(), e);
        }

        documentRepository.delete(document);

        auditLogService.logSystemAction("DOCUMENT_DELETED", "EMPLOYEE_DOCUMENT",
                "Document deleted for employee " + employeeId + ": " + document.getDocumentType());

        logger.info("Document deleted: {}", documentId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponse> getExpiringSoon(int days) {
        LocalDate now = LocalDate.now();
        LocalDate threshold = now.plusDays(days);
        return documentRepository.findExpiringSoon(now, threshold).stream()
                .map(EmployeeDocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponse> getExpired() {
        return documentRepository.findExpired(LocalDate.now()).stream()
                .map(EmployeeDocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
    }
}

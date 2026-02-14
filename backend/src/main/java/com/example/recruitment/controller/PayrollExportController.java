package com.example.recruitment.controller;

import com.example.recruitment.dto.PayrollPackage;
import com.example.recruitment.service.PayrollExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class PayrollExportController {

    @Autowired
    private PayrollExportService payrollExportService;

    @GetMapping("/offers/{offerId}/package")
    public ResponseEntity<?> getPayrollPackage(@PathVariable Long offerId) {
        PayrollPackage pkg = payrollExportService.generatePayrollPackage(offerId);
        return ResponseEntity.ok(pkg);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestBody Map<String, Object> request) {
        List<Number> offerIdNumbers = (List<Number>) request.get("offerIds");
        List<Long> offerIds = offerIdNumbers.stream()
            .map(Number::longValue)
            .toList();

        List<PayrollPackage> packages = payrollExportService.generateBulkPayrollPackages(offerIds);
        byte[] csv = payrollExportService.exportToCsv(packages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "payroll-export.csv");

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/export/json")
    public ResponseEntity<byte[]> exportJson(@RequestBody Map<String, Object> request) {
        List<Number> offerIdNumbers = (List<Number>) request.get("offerIds");
        List<Long> offerIds = offerIdNumbers.stream()
            .map(Number::longValue)
            .toList();

        List<PayrollPackage> packages = payrollExportService.generateBulkPayrollPackages(offerIds);
        byte[] json = payrollExportService.exportToJson(packages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "payroll-export.json");

        return new ResponseEntity<>(json, headers, HttpStatus.OK);
    }

    @GetMapping("/export/summary")
    public ResponseEntity<?> getExportSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(payrollExportService.getExportSummary(startDate, endDate));
    }
}

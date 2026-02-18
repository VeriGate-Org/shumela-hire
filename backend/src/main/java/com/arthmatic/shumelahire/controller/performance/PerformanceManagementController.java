package com.arthmatic.shumelahire.controller.performance;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.entity.performance.PerformanceCycle;
import com.arthmatic.shumelahire.entity.performance.PerformanceContract;
import com.arthmatic.shumelahire.entity.performance.PerformanceTemplate;
import com.arthmatic.shumelahire.service.performance.PerformanceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/performance")
@Validated
public class PerformanceManagementController {

    @Autowired
    private PerformanceManagementService performanceService;

    // ========== PERFORMANCE CYCLES ENDPOINTS ==========

    @PostMapping("/cycles")
    public ResponseEntity<PerformanceCycle> createCycle(
            @Valid @RequestBody PerformanceManagementService.CreateCycleRequest request,
            @RequestHeader("X-User-Id") String userId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            PerformanceCycle cycle = performanceService.createCycle(request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cycle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cycles")
    public ResponseEntity<Page<PerformanceCycle>> getCycles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String tenantId = TenantContext.requireCurrentTenant();
        Pageable pageable = PageRequest.of(page, size);
        Page<PerformanceCycle> cycles = performanceService.getCycles(tenantId, pageable);
        return ResponseEntity.ok(cycles);
    }

    @GetMapping("/cycles/{id}")
    public ResponseEntity<PerformanceCycle> getCycle(@PathVariable Long id) {

        String tenantId = TenantContext.requireCurrentTenant();
        Optional<PerformanceCycle> cycle = performanceService.getCycle(id, tenantId);
        return cycle.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cycles/{id}/activate")
    public ResponseEntity<Void> activateCycle(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            performanceService.activateCycle(id, tenantId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== PERFORMANCE CONTRACTS ENDPOINTS ==========

    @PostMapping("/contracts")
    public ResponseEntity<PerformanceContract> createContract(
            @Valid @RequestBody PerformanceManagementService.CreateContractRequest request) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            PerformanceContract contract = performanceService.createContract(request, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(contract);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/contracts")
    public ResponseEntity<Page<PerformanceContract>> getContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String tenantId = TenantContext.requireCurrentTenant();
        Pageable pageable = PageRequest.of(page, size);
        Page<PerformanceContract> contracts = performanceService.getContracts(tenantId, pageable);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/contracts/{id}")
    public ResponseEntity<PerformanceContract> getContract(@PathVariable Long id) {

        String tenantId = TenantContext.requireCurrentTenant();
        Optional<PerformanceContract> contract = performanceService.getContract(id, tenantId);
        return contract.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/contracts/{id}/submit")
    public ResponseEntity<Void> submitContract(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            performanceService.submitContract(id, tenantId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/contracts/{id}/approve")
    public ResponseEntity<Void> approveContract(
            @PathVariable Long id,
            @RequestBody ApprovalRequest request,
            @RequestHeader("X-User-Id") String approverId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            performanceService.approveContract(id, approverId, request.getComments(), tenantId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== PERFORMANCE TEMPLATES ENDPOINTS ==========

    @PostMapping("/templates")
    public ResponseEntity<PerformanceTemplate> createTemplate(
            @Valid @RequestBody PerformanceManagementService.CreateTemplateRequest request,
            @RequestHeader("X-User-Id") String userId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            PerformanceTemplate template = performanceService.createTemplate(request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(template);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<Page<PerformanceTemplate>> getTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String tenantId = TenantContext.requireCurrentTenant();
        Pageable pageable = PageRequest.of(page, size);
        Page<PerformanceTemplate> templates = performanceService.getTemplates(tenantId, pageable);
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/templates/{id}")
    public ResponseEntity<PerformanceTemplate> getTemplate(@PathVariable Long id) {

        String tenantId = TenantContext.requireCurrentTenant();
        Optional<PerformanceTemplate> template = performanceService.getTemplate(id, tenantId);
        return template.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    public static class ApprovalRequest {
        private String comments;

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
    }
}

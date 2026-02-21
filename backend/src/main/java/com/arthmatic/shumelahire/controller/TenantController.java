package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.entity.Tenant;
import com.arthmatic.shumelahire.repository.TenantRepository;
import com.arthmatic.shumelahire.service.TenantOnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/tenants")
@PreAuthorize("hasRole('ADMIN')")
public class TenantController {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantOnboardingService onboardingService;

    @PostMapping
    public ResponseEntity<Tenant> createTenant(
            @Valid @RequestBody TenantOnboardingService.CreateTenantRequest request) {
        try {
            Tenant tenant = onboardingService.createTenant(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<Tenant>> listTenants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Tenant> tenants = tenantRepository.findAll(pageable);
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable String id) {
        Optional<Tenant> tenant = tenantRepository.findById(id);
        return tenant.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(
            @PathVariable String id,
            @Valid @RequestBody TenantOnboardingService.UpdateTenantRequest request) {
        try {
            Tenant tenant = onboardingService.updateTenant(id, request);
            return ResponseEntity.ok(tenant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendTenant(@PathVariable String id) {
        try {
            onboardingService.suspendTenant(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateTenant(@PathVariable String id) {
        try {
            onboardingService.activateTenant(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String id) {
        if (!tenantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tenantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.ShiftSwapRequestDto;
import com.arthmatic.shumelahire.dto.attendance.ShiftSwapResponse;
import com.arthmatic.shumelahire.service.attendance.ShiftSwapService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shift-swaps")
public class ShiftSwapController {

    private final ShiftSwapService shiftSwapService;

    public ShiftSwapController(ShiftSwapService shiftSwapService) {
        this.shiftSwapService = shiftSwapService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> createRequest(
            @RequestParam Long requesterId,
            @Valid @RequestBody ShiftSwapRequestDto request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(shiftSwapService.createRequest(requesterId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/target-response")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> respondAsTarget(
            @PathVariable Long id,
            @RequestParam Long targetEmployeeId,
            @RequestParam boolean accept,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(shiftSwapService.respondAsTarget(id, targetEmployeeId, accept, notes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/manager-approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> approveAsManager(
            @PathVariable Long id,
            @RequestParam Long managerId,
            @RequestParam boolean approve,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(shiftSwapService.approveAsManager(id, managerId, approve, notes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> cancel(@PathVariable Long id, @RequestParam Long requesterId) {
        try {
            return ResponseEntity.ok(shiftSwapService.cancel(id, requesterId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/requester/{requesterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<ShiftSwapResponse>> getByRequester(
            @PathVariable Long requesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(shiftSwapService.getByRequester(requesterId, PageRequest.of(page, size)));
    }

    @GetMapping("/pending-target/{targetId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<ShiftSwapResponse>> getPendingForTarget(@PathVariable Long targetId) {
        return ResponseEntity.ok(shiftSwapService.getPendingForTarget(targetId));
    }

    @GetMapping("/pending-manager")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<ShiftSwapResponse>> getPendingManagerApproval(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(shiftSwapService.getPendingManagerApproval(PageRequest.of(page, size)));
    }
}

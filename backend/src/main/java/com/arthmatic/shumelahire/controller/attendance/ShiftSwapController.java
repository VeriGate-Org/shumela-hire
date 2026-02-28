package com.arthmatic.shumelahire.controller.attendance;

import com.arthmatic.shumelahire.dto.attendance.ShiftSwapRequestDto;
import com.arthmatic.shumelahire.dto.attendance.ShiftSwapResponse;
import com.arthmatic.shumelahire.service.attendance.ShiftSwapService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-swaps")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
public class ShiftSwapController {

    private static final Logger logger = LoggerFactory.getLogger(ShiftSwapController.class);

    @Autowired
    private ShiftSwapService shiftSwapService;

    @PostMapping
    public ResponseEntity<?> createSwapRequest(@Valid @RequestBody ShiftSwapRequestDto request) {
        try {
            ShiftSwapResponse response = shiftSwapService.createSwapRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating shift swap request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSwapRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftSwapService.getSwapRequest(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShiftSwapResponse>> getSwapsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(shiftSwapService.getSwapsByEmployee(employeeId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<ShiftSwapResponse>> getPendingSwaps() {
        return ResponseEntity.ok(shiftSwapService.getPendingSwaps());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> approveSwap(@PathVariable Long id, @RequestParam String approver) {
        try {
            return ResponseEntity.ok(shiftSwapService.approveSwap(id, approver));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> rejectSwap(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(shiftSwapService.rejectSwap(id, approver, reason));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSwap(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftSwapService.cancelSwap(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    static class ErrorResponse {
        private final String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}

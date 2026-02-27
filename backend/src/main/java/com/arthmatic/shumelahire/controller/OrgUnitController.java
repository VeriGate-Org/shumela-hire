package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.org.OrgUnitRequest;
import com.arthmatic.shumelahire.dto.org.OrgUnitResponse;
import com.arthmatic.shumelahire.service.OrgUnitService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/org/units")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class OrgUnitController {

    private static final Logger logger = LoggerFactory.getLogger(OrgUnitController.class);

    @Autowired
    private OrgUnitService orgUnitService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createOrgUnit(@Valid @RequestBody OrgUnitRequest request) {
        try {
            OrgUnitResponse response = orgUnitService.createOrgUnit(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating org unit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateOrgUnit(@PathVariable Long id,
                                           @Valid @RequestBody OrgUnitRequest request) {
        try {
            OrgUnitResponse response = orgUnitService.updateOrgUnit(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating org unit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrgUnit(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orgUnitService.getOrgUnit(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting org unit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<List<OrgUnitResponse>> getAllOrgUnits() {
        return ResponseEntity.ok(orgUnitService.getAllOrgUnits());
    }

    @GetMapping("/tree")
    public ResponseEntity<List<OrgUnitResponse>> getOrgTree() {
        return ResponseEntity.ok(orgUnitService.getOrgTree());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<OrgUnitResponse>> getChildUnits(@PathVariable Long parentId) {
        return ResponseEntity.ok(orgUnitService.getChildUnits(parentId));
    }

    @PatchMapping("/{id}/move")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> moveOrgUnit(@PathVariable Long id,
                                          @RequestBody Map<String, Long> body) {
        try {
            Long newParentId = body.get("parentId");
            OrgUnitResponse response = orgUnitService.moveOrgUnit(id, newParentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error moving org unit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deleteOrgUnit(@PathVariable Long id) {
        try {
            orgUnitService.deleteOrgUnit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting org unit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}

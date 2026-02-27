package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.ad.ADGroupRoleMappingRequest;
import com.arthmatic.shumelahire.dto.ad.ADGroupRoleMappingResponse;
import com.arthmatic.shumelahire.dto.ad.ADSyncLogResponse;
import com.arthmatic.shumelahire.entity.ADGroupRoleMapping;
import com.arthmatic.shumelahire.entity.ADSyncLog;
import com.arthmatic.shumelahire.service.ad.ADGroupMappingService;
import com.arthmatic.shumelahire.service.ad.ActiveDirectoryAuthService;
import com.arthmatic.shumelahire.service.ad.ActiveDirectorySyncService;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.arthmatic.shumelahire.repository.ADSyncLogRepository;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/ad")
@ConditionalOnBean(ActiveDirectoryAuthService.class)
public class ADAdminController {

    private final ActiveDirectoryAuthService adAuthService;
    private final ActiveDirectorySyncService adSyncService;
    private final ADGroupMappingService groupMappingService;
    private final ADSyncLogRepository syncLogRepository;

    public ADAdminController(ActiveDirectoryAuthService adAuthService,
                              ActiveDirectorySyncService adSyncService,
                              ADGroupMappingService groupMappingService,
                              ADSyncLogRepository syncLogRepository) {
        this.adAuthService = adAuthService;
        this.adSyncService = adSyncService;
        this.groupMappingService = groupMappingService;
        this.syncLogRepository = syncLogRepository;
    }

    // --- AD Group Discovery ---

    @GetMapping("/groups")
    public ResponseEntity<List<Map<String, String>>> listAdGroups() {
        List<Map<String, String>> groups = adAuthService.listAdGroups();
        return ResponseEntity.ok(groups);
    }

    // --- Group-to-Role Mappings ---

    @GetMapping("/group-mappings")
    public ResponseEntity<List<ADGroupRoleMappingResponse>> getAllMappings() {
        List<ADGroupRoleMappingResponse> mappings = groupMappingService.getAllMappings()
                .stream()
                .map(ADGroupRoleMappingResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mappings);
    }

    @PostMapping("/group-mappings")
    public ResponseEntity<ADGroupRoleMappingResponse> createMapping(
            @Valid @RequestBody ADGroupRoleMappingRequest request) {
        ADGroupRoleMapping mapping = groupMappingService.createMapping(request);
        return ResponseEntity.ok(ADGroupRoleMappingResponse.from(mapping));
    }

    @PutMapping("/group-mappings/{id}")
    public ResponseEntity<ADGroupRoleMappingResponse> updateMapping(
            @PathVariable Long id,
            @Valid @RequestBody ADGroupRoleMappingRequest request) {
        ADGroupRoleMapping mapping = groupMappingService.updateMapping(id, request);
        return ResponseEntity.ok(ADGroupRoleMappingResponse.from(mapping));
    }

    @DeleteMapping("/group-mappings/{id}")
    public ResponseEntity<Void> deleteMapping(@PathVariable Long id) {
        groupMappingService.deleteMapping(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/group-mappings/{id}/toggle")
    public ResponseEntity<ADGroupRoleMappingResponse> toggleMapping(
            @PathVariable Long id,
            @RequestParam boolean active) {
        ADGroupRoleMapping mapping = groupMappingService.toggleMapping(id, active);
        return ResponseEntity.ok(ADGroupRoleMappingResponse.from(mapping));
    }

    // --- Sync Operations ---

    @PostMapping("/sync")
    public ResponseEntity<ADSyncLogResponse> triggerSync(
            @RequestParam(defaultValue = "FULL") ADSyncLog.SyncType syncType,
            Principal principal) {
        String triggeredBy = principal != null ? principal.getName() : "MANUAL";
        ADSyncLog syncLog = adSyncService.triggerSync(syncType, triggeredBy);
        return ResponseEntity.ok(ADSyncLogResponse.from(syncLog));
    }

    @GetMapping("/sync/logs")
    public ResponseEntity<Page<ADSyncLogResponse>> getSyncLogs(Pageable pageable) {
        Page<ADSyncLogResponse> logs = syncLogRepository.findAllByOrderByStartedAtDesc(pageable)
                .map(ADSyncLogResponse::from);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/sync/latest")
    public ResponseEntity<?> getLatestSync() {
        return syncLogRepository.findTopByOrderByStartedAtDesc()
                .map(log -> ResponseEntity.ok(ADSyncLogResponse.from(log)))
                .orElse(ResponseEntity.noContent().build());
    }
}

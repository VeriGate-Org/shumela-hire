package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.entity.TalentPool;
import com.arthmatic.shumelahire.entity.TalentPoolEntry;
import com.arthmatic.shumelahire.service.TalentPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/talent-pools")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'RECRUITER')")
public class TalentPoolController {

    @Autowired
    private TalentPoolService talentPoolService;

    @PostMapping
    public ResponseEntity<?> createPool(@RequestBody TalentPool pool) {
        return ResponseEntity.status(HttpStatus.CREATED).body(talentPoolService.createPool(pool));
    }

    @GetMapping
    public ResponseEntity<?> getAllPools() {
        return ResponseEntity.ok(talentPoolService.getAllPools());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPool(@PathVariable Long id) {
        return ResponseEntity.ok(talentPoolService.getPool(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePool(@PathVariable Long id, @RequestBody TalentPool pool) {
        return ResponseEntity.ok(talentPoolService.updatePool(id, pool));
    }

    @PostMapping("/{poolId}/entries")
    public ResponseEntity<?> addEntry(
            @PathVariable Long poolId,
            @RequestBody TalentPoolEntry entry) {
        return ResponseEntity.status(HttpStatus.CREATED).body(talentPoolService.addEntry(poolId, entry));
    }

    @GetMapping("/{poolId}/entries")
    public ResponseEntity<?> getEntries(@PathVariable Long poolId) {
        return ResponseEntity.ok(talentPoolService.getEntries(poolId));
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> removeEntry(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Removed by user") String reason) {
        talentPoolService.removeEntry(id, reason);
        return ResponseEntity.ok(Map.of("message", "Entry removed"));
    }

    @PutMapping("/entries/{id}/rating")
    public ResponseEntity<?> updateRating(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        return ResponseEntity.ok(talentPoolService.updateRating(id, request.get("rating")));
    }

    @GetMapping("/{poolId}/analytics")
    public ResponseEntity<?> getPoolAnalytics(@PathVariable Long poolId) {
        return ResponseEntity.ok(talentPoolService.getPoolAnalytics(poolId));
    }
}

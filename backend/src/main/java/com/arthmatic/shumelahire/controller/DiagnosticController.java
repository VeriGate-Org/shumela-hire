package com.arthmatic.shumelahire.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TEMPORARY diagnostic endpoint — DELETE after IDC demo seeding is confirmed.
 * Public (no auth, no tenant) to allow quick deployment verification.
 */
@RestController
@RequestMapping("/api/public/diag")
public class DiagnosticController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/{subdomain}")
    public ResponseEntity<Map<String, Object>> diagnose(@PathVariable String subdomain) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // 1. Flyway schema version
            Object[] fwRow = (Object[]) em.createNativeQuery(
                    "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1")
                    .getSingleResult();
            result.put("flyway_version", fwRow[0]);
            result.put("flyway_description", fwRow[1]);
            result.put("flyway_success", fwRow[2]);

            // 1b. Check if V021 exists in history
            List<?> v021 = em.createNativeQuery(
                    "SELECT version, success, installed_on FROM flyway_schema_history WHERE version = '021'")
                    .getResultList();
            result.put("v021_applied", !v021.isEmpty());
            if (!v021.isEmpty()) {
                Object[] r = (Object[]) v021.get(0);
                result.put("v021_success", r[1]);
                result.put("v021_installed_on", r[2] != null ? r[2].toString() : null);
            }
        } catch (Exception e) {
            result.put("flyway_error", e.getMessage());
        }

        try {
            // 2. Tenant lookup
            List<?> tenants = em.createNativeQuery(
                    "SELECT id, name, status FROM tenants WHERE subdomain = :sub")
                    .setParameter("sub", subdomain)
                    .getResultList();
            if (tenants.isEmpty()) {
                result.put("tenant", "NOT FOUND");
                return ResponseEntity.ok(result);
            }
            Object[] t = (Object[]) tenants.get(0);
            String tenantId = (String) t[0];
            result.put("tenant_id", tenantId);
            result.put("tenant_name", t[1]);
            result.put("tenant_status", t[2]);

            // 3. Data counts
            String[][] tables = {
                    {"departments", "tenant_id"},
                    {"applicants", "tenant_id"},
                    {"job_postings", "tenant_id"},
                    {"applications", "tenant_id"},
                    {"interviews", "tenant_id"},
                    {"offers", "tenant_id"},
                    {"talent_pools", "tenant_id"},
                    {"talent_pool_entries", "tenant_id"},
                    {"agency_profiles", "tenant_id"},
                    {"requisitions", "tenant_id"},
            };
            Map<String, Object> counts = new LinkedHashMap<>();
            for (String[] table : tables) {
                try {
                    Object count = em.createNativeQuery(
                            "SELECT COUNT(*) FROM " + table[0] + " WHERE " + table[1] + " = :tid")
                            .setParameter("tid", tenantId)
                            .getSingleResult();
                    counts.put(table[0], count);
                } catch (Exception e) {
                    counts.put(table[0], "ERROR: " + e.getMessage());
                }
            }
            result.put("counts", counts);

        } catch (Exception e) {
            result.put("tenant_error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}

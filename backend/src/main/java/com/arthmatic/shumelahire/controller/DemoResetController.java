package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TEMPORARY controller for clearing demo tenant data before re-seeding.
 * DELETE THIS FILE after the IDC demo seeding is complete.
 */
@RestController
@RequestMapping("/api/admin/demo-reset")
@PreAuthorize("hasRole('ADMIN')")
public class DemoResetController {

    private static final Logger log = LoggerFactory.getLogger(DemoResetController.class);

    @PersistenceContext
    private EntityManager em;

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> resetDemoData(
            @RequestParam(defaultValue = "false") boolean confirm) {

        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Pass ?confirm=true to execute the reset",
                    "warning", "This will DELETE all recruitment data for the current tenant"
            ));
        }

        String tenantId = TenantContext.requireCurrentTenant();
        log.warn("DEMO RESET initiated for tenant: {}", tenantId);

        Map<String, Object> results = new LinkedHashMap<>();
        int total = 0;

        // Delete in FK-safe order (children before parents)
        String[][] tables = {
                // Performance tables
                {"sap_payroll_transmissions", "tenant_id"},
                {"review_evidence", "tenant_id"},
                {"review_goal_scores", "tenant_id"},
                {"goal_kpis", "tenant_id"},
                {"performance_reviews", "tenant_id"},
                {"performance_goals", "tenant_id"},
                {"performance_contracts", "tenant_id"},
                {"performance_cycles", "tenant_id"},
                {"performance_templates", "tenant_id"},
                // Recruitment leaf tables
                {"screening_answers", "tenant_id"},
                {"screening_questions", "tenant_id"},
                {"background_checks", "tenant_id"},
                {"shortlist_scores", "tenant_id"},
                {"pipeline_transitions", "tenant_id"},
                {"tg_salary_recommendations", "tenant_id"},
                {"recruitment_metrics", "tenant_id"},
                // Interviews & offers
                {"interviews", "tenant_id"},
                {"offers", "tenant_id"},
                // Agency submissions
                {"agency_submissions", "tenant_id"},
                // Talent pool entries
                {"talent_pool_entries", "tenant_id"},
                // Documents
                {"documents", "tenant_id"},
                // Employee tables
                {"custom_field_values", "tenant_id"},
                {"employee_documents", "tenant_id"},
                {"employment_events", "tenant_id"},
                {"employees", "tenant_id"},
                {"custom_fields", "tenant_id"},
                // Applications & applicants
                {"applications", "tenant_id"},
                {"applicants", "tenant_id"},
                // Job-related
                {"tg_job_board_postings", "tenant_id"},
                {"job_ad_history", "tenant_id"},
                {"job_ads", "tenant_id"},
                {"job_postings", "tenant_id"},
                // Talent pools & agencies
                {"talent_pools", "tenant_id"},
                {"agency_profiles", "tenant_id"},
                // Requisitions & departments
                {"requisitions", "tenant_id"},
                {"departments", "tenant_id"},
                // Supporting tables
                {"workflow_executions", "tenant_id"},
                {"workflow_definitions", "tenant_id"},
                {"messages", "tenant_id"},
                {"notifications", "tenant_id"},
                {"audit_logs", "tenant_id"},
                {"user_preferences", "tenant_id"},
                {"linkedin_org_connections", "tenant_id"},
        };

        for (String[] table : tables) {
            try {
                String sql = "DELETE FROM " + table[0] + " WHERE " + table[1] + " = :tenantId";
                int deleted = em.createNativeQuery(sql)
                        .setParameter("tenantId", tenantId)
                        .executeUpdate();
                if (deleted > 0) {
                    results.put(table[0], deleted);
                    total += deleted;
                    log.info("Deleted {} rows from {}", deleted, table[0]);
                }
            } catch (Exception e) {
                results.put(table[0] + "_error", e.getMessage());
                log.warn("Failed to clear {}: {}", table[0], e.getMessage());
            }
        }

        // Delete non-admin users (preserve the admin account running this)
        try {
            int usersDeleted = em.createNativeQuery(
                            "DELETE FROM users WHERE tenant_id = :tenantId AND role != 'ADMIN'")
                    .setParameter("tenantId", tenantId)
                    .executeUpdate();
            if (usersDeleted > 0) {
                results.put("users (non-admin)", usersDeleted);
                total += usersDeleted;
            }
        } catch (Exception e) {
            results.put("users_error", e.getMessage());
            log.warn("Failed to clear non-admin users: {}", e.getMessage());
        }

        results.put("_total_deleted", total);
        results.put("_tenant", tenantId);
        log.warn("DEMO RESET complete for tenant {}: {} total rows deleted", tenantId, total);

        return ResponseEntity.ok(results);
    }
}

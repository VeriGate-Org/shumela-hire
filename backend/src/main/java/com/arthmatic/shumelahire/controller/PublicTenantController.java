package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.entity.Tenant;
import com.arthmatic.shumelahire.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/public/tenants")
public class PublicTenantController {

    @Autowired
    private TenantRepository tenantRepository;

    @GetMapping("/resolve/{subdomain}")
    public ResponseEntity<TenantInfo> resolveTenant(@PathVariable String subdomain) {
        Optional<Tenant> tenant = tenantRepository.findBySubdomain(subdomain);

        return tenant
                .filter(Tenant::isActive)
                .map(t -> ResponseEntity.ok(new TenantInfo(t.getId(), t.getName(), t.getSubdomain(), t.getPlan())))
                .orElse(ResponseEntity.notFound().build());
    }

    public static class TenantInfo {
        private final String id;
        private final String name;
        private final String subdomain;
        private final String plan;

        public TenantInfo(String id, String name, String subdomain, String plan) {
            this.id = id;
            this.name = name;
            this.subdomain = subdomain;
            this.plan = plan;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getSubdomain() { return subdomain; }
        public String getPlan() { return plan; }
    }
}

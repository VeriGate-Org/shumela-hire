package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LinkedInOrgConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkedInOrgConnectionRepository extends JpaRepository<LinkedInOrgConnection, Long> {

    Optional<LinkedInOrgConnection> findByTenantId(String tenantId);

    void deleteByTenantId(String tenantId);
}

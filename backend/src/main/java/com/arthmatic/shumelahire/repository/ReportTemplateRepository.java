package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findBySharedTrueOrCreatedByOrderByUpdatedAtDesc(String createdBy);

    List<ReportTemplate> findBySystemTrueOrderByNameAsc();

    boolean existsByNameAndCreatedBy(String name, String createdBy);
}

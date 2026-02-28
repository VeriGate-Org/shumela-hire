package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.PlatformFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformFeatureRepository extends JpaRepository<PlatformFeature, Long> {

    Optional<PlatformFeature> findByCode(String code);

    List<PlatformFeature> findByIsActiveTrue();

    List<PlatformFeature> findByCategory(String category);

    boolean existsByCode(String code);
}

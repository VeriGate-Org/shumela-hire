package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.TalentPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TalentPoolRepository extends JpaRepository<TalentPool, Long> {

    Optional<TalentPool> findByPoolName(String poolName);

    List<TalentPool> findByIsActiveTrue();

    @Query("SELECT tp FROM TalentPool tp WHERE tp.autoAddEnabled = true AND tp.isActive = true")
    List<TalentPool> findAutoAddPools();
}

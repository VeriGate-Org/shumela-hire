package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.TalentPoolEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalentPoolEntryRepository extends JpaRepository<TalentPoolEntry, Long> {

    @Query("SELECT e FROM TalentPoolEntry e WHERE e.talentPool.id = :poolId AND e.removedAt IS NULL AND e.isAvailable = true ORDER BY e.rating DESC NULLS LAST")
    List<TalentPoolEntry> findAvailableCandidates(@Param("poolId") Long poolId);

    @Query("SELECT COUNT(e) FROM TalentPoolEntry e WHERE e.talentPool.id = :poolId AND e.removedAt IS NULL")
    long countActive(@Param("poolId") Long poolId);

    @Query("SELECT e FROM TalentPoolEntry e WHERE e.talentPool.id = :poolId AND e.removedAt IS NULL")
    List<TalentPoolEntry> findByTalentPoolId(@Param("poolId") Long poolId);

    List<TalentPoolEntry> findByApplicantId(Long applicantId);
}

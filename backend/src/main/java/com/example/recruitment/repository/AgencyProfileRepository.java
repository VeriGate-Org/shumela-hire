package com.example.recruitment.repository;

import com.example.recruitment.entity.AgencyProfile;
import com.example.recruitment.entity.AgencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyProfileRepository extends JpaRepository<AgencyProfile, Long> {

    Optional<AgencyProfile> findByContactEmail(String contactEmail);

    List<AgencyProfile> findByStatus(AgencyStatus status);

    @Query("SELECT a FROM AgencyProfile a WHERE a.status = 'ACTIVE'")
    List<AgencyProfile> findActiveAgencies();
}

package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    List<Geofence> findByActiveTrue();

    List<Geofence> findBySite(String site);
}

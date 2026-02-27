package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    List<Geofence> findByIsActiveTrue();

    Optional<Geofence> findBySiteCode(String siteCode);

    List<Geofence> findByCity(String city);

    List<Geofence> findByProvince(String province);
}

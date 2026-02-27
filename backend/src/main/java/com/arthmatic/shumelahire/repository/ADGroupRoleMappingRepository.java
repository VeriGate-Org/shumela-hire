package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ADGroupRoleMapping;
import com.arthmatic.shumelahire.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ADGroupRoleMappingRepository extends JpaRepository<ADGroupRoleMapping, Long> {

    List<ADGroupRoleMapping> findByIsActiveTrue();

    Optional<ADGroupRoleMapping> findByAdGroupDN(String adGroupDN);

    List<ADGroupRoleMapping> findByShumelaRole(User.Role role);

    boolean existsByAdGroupDN(String adGroupDN);
}

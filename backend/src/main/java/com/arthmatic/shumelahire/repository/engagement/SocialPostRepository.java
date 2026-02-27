package com.arthmatic.shumelahire.repository.engagement;

import com.arthmatic.shumelahire.entity.engagement.SocialPost;
import com.arthmatic.shumelahire.entity.engagement.SocialPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialPostRepository extends JpaRepository<SocialPost, Long> {

    @Query("SELECT sp FROM SocialPost sp WHERE sp.tenantId = :tenantId ORDER BY sp.isPinned DESC, sp.createdAt DESC")
    Page<SocialPost> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);

    List<SocialPost> findByAuthorId(Long authorId);

    @Query("SELECT sp FROM SocialPost sp WHERE sp.tenantId = :tenantId AND sp.postType = :postType ORDER BY sp.createdAt DESC")
    Page<SocialPost> findByTenantIdAndPostType(@Param("tenantId") String tenantId, @Param("postType") SocialPostType postType, Pageable pageable);

    @Query("SELECT sp FROM SocialPost sp WHERE sp.tenantId = :tenantId AND sp.isPinned = true ORDER BY sp.createdAt DESC")
    List<SocialPost> findPinnedByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(sp) FROM SocialPost sp WHERE sp.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") String tenantId);
}

package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.SocialPostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SocialPostRequest {

    @NotNull(message = "Author ID is required")
    private Long authorId;

    private SocialPostType postType = SocialPostType.UPDATE;

    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Boolean isPinned = false;

    // Getters and Setters
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public SocialPostType getPostType() { return postType; }
    public void setPostType(SocialPostType postType) { this.postType = postType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }
}

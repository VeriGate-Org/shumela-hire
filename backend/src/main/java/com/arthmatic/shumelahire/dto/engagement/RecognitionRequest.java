package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.RecognitionBadge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RecognitionRequest {

    @NotNull(message = "Giver ID is required")
    private Long giverId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Badge is required")
    private RecognitionBadge badge;

    @NotBlank(message = "Message is required")
    private String message;

    private Integer points;
    private Boolean isPublic = true;

    // Getters and Setters
    public Long getGiverId() { return giverId; }
    public void setGiverId(Long giverId) { this.giverId = giverId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public RecognitionBadge getBadge() { return badge; }
    public void setBadge(RecognitionBadge badge) { this.badge = badge; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}

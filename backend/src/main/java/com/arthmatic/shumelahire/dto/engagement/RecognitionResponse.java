package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.Recognition;
import com.arthmatic.shumelahire.entity.engagement.RecognitionBadge;

import java.time.LocalDateTime;

public class RecognitionResponse {

    private Long id;
    private Long giverId;
    private String giverName;
    private Long receiverId;
    private String receiverName;
    private RecognitionBadge badge;
    private String message;
    private Integer points;
    private Boolean isPublic;
    private LocalDateTime createdAt;

    public static RecognitionResponse fromEntity(Recognition recognition) {
        RecognitionResponse dto = new RecognitionResponse();
        dto.setId(recognition.getId());
        if (recognition.getGiver() != null) {
            dto.setGiverId(recognition.getGiver().getId());
            dto.setGiverName(recognition.getGiver().getFullName());
        }
        if (recognition.getReceiver() != null) {
            dto.setReceiverId(recognition.getReceiver().getId());
            dto.setReceiverName(recognition.getReceiver().getFullName());
        }
        dto.setBadge(recognition.getBadge());
        dto.setMessage(recognition.getMessage());
        dto.setPoints(recognition.getPoints());
        dto.setIsPublic(recognition.getIsPublic());
        dto.setCreatedAt(recognition.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGiverId() { return giverId; }
    public void setGiverId(Long giverId) { this.giverId = giverId; }

    public String getGiverName() { return giverName; }
    public void setGiverName(String giverName) { this.giverName = giverName; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public RecognitionBadge getBadge() { return badge; }
    public void setBadge(RecognitionBadge badge) { this.badge = badge; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

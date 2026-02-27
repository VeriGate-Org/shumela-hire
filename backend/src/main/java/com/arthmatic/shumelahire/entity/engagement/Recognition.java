package com.arthmatic.shumelahire.entity.engagement;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "recognitions")
public class Recognition extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id", nullable = false)
    @NotNull
    private Employee giver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @NotNull
    private Employee receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge", nullable = false, length = 50)
    @NotNull
    private RecognitionBadge badge;

    @NotBlank
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Min(1) @Max(100)
    @Column(name = "points", nullable = false)
    private Integer points = 10;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Recognition() {}

    public int getPointsForBadge() {
        return switch (badge) {
            case STAR_PERFORMER, EXTRA_MILE -> 25;
            case LEADER, INNOVATOR, MENTOR -> 20;
            case TEAM_PLAYER, PROBLEM_SOLVER, CULTURE_CHAMPION, CUSTOMER_HERO -> 15;
            case HELPER -> 10;
        };
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getGiver() { return giver; }
    public void setGiver(Employee giver) { this.giver = giver; }

    public Employee getReceiver() { return receiver; }
    public void setReceiver(Employee receiver) { this.receiver = receiver; }

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

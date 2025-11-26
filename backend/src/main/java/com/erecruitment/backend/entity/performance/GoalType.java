package com.erecruitment.backend.entity.performance;

public enum GoalType {
    STRATEGIC("Strategic"),
    OPERATIONAL("Operational"),
    DEVELOPMENT("Development"),
    BEHAVIORAL("Behavioral");
    
    private final String displayName;
    
    GoalType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
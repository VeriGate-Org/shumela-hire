package com.arthmatic.talentgate.entity.performance;

public enum KPIType {
    QUANTITATIVE("Quantitative"),
    QUALITATIVE("Qualitative"),
    BEHAVIORAL("Behavioral");
    
    private final String displayName;
    
    KPIType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
package com.example.recruitment.entity;

public enum ExperienceLevel {
    ENTRY_LEVEL("Entry Level", "0-2 years of experience", 0, 2),
    JUNIOR("Junior", "1-3 years of experience", 1, 3),
    MID_LEVEL("Mid-Level", "3-6 years of experience", 3, 6),
    SENIOR("Senior", "6-10 years of experience", 6, 10),
    LEAD("Lead", "8+ years with leadership experience", 8, 15),
    EXECUTIVE("Executive", "10+ years with executive experience", 10, 25),
    EXPERT("Expert", "15+ years of specialized expertise", 15, 30);
    
    private final String displayName;
    private final String description;
    private final int minYears;
    private final int maxYears;
    
    ExperienceLevel(String displayName, String description, int minYears, int maxYears) {
        this.displayName = displayName;
        this.description = description;
        this.minYears = minYears;
        this.maxYears = maxYears;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getMinYears() {
        return minYears;
    }
    
    public int getMaxYears() {
        return maxYears;
    }
    
    // Get CSS class for styling
    public String getCssClass() {
        switch (this) {
            case ENTRY_LEVEL:
                return "bg-green-100 text-green-800";
            case JUNIOR:
                return "bg-blue-100 text-blue-800";
            case MID_LEVEL:
                return "bg-yellow-100 text-yellow-800";
            case SENIOR:
                return "bg-orange-100 text-orange-800";
            case LEAD:
                return "bg-purple-100 text-purple-800";
            case EXECUTIVE:
                return "bg-red-100 text-red-800";
            case EXPERT:
                return "bg-indigo-100 text-indigo-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }
    
    // Get icon for experience level
    public String getIcon() {
        switch (this) {
            case ENTRY_LEVEL:
                return "🌱";
            case JUNIOR:
                return "🌿";
            case MID_LEVEL:
                return "🌳";
            case SENIOR:
                return "🏆";
            case LEAD:
                return "👑";
            case EXECUTIVE:
                return "💎";
            case EXPERT:
                return "🎯";
            default:
                return "📊";
        }
    }
    
    // Helper method to determine experience level from years
    public static ExperienceLevel fromYears(int years) {
        if (years <= 2) return ENTRY_LEVEL;
        if (years <= 3) return JUNIOR;
        if (years <= 6) return MID_LEVEL;
        if (years <= 10) return SENIOR;
        if (years <= 15) return LEAD;
        if (years <= 25) return EXECUTIVE;
        return EXPERT;
    }
}
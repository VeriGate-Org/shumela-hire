package com.example.recruitment.entity;

public enum EmploymentType {
    FULL_TIME("Full-time", "Standard full-time employment"),
    PART_TIME("Part-time", "Part-time employment with reduced hours"),
    CONTRACT("Contract", "Fixed-term contract position"),
    TEMPORARY("Temporary", "Temporary position for specific duration"),
    FREELANCE("Freelance", "Independent contractor/freelance work"),
    INTERNSHIP("Internship", "Internship or training position"),
    APPRENTICESHIP("Apprenticeship", "Formal apprenticeship program"),
    VOLUNTEER("Volunteer", "Volunteer position");
    
    private final String displayName;
    private final String description;
    
    EmploymentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Get CSS class for styling
    public String getCssClass() {
        switch (this) {
            case FULL_TIME:
                return "bg-blue-100 text-blue-800";
            case PART_TIME:
                return "bg-green-100 text-green-800";
            case CONTRACT:
                return "bg-purple-100 text-purple-800";
            case TEMPORARY:
                return "bg-yellow-100 text-yellow-800";
            case FREELANCE:
                return "bg-indigo-100 text-indigo-800";
            case INTERNSHIP:
                return "bg-orange-100 text-orange-800";
            case APPRENTICESHIP:
                return "bg-pink-100 text-pink-800";
            case VOLUNTEER:
                return "bg-gray-100 text-gray-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }
    
    // Get icon for employment type
    public String getIcon() {
        switch (this) {
            case FULL_TIME:
                return "💼";
            case PART_TIME:
                return "⏱️";
            case CONTRACT:
                return "📋";
            case TEMPORARY:
                return "⏰";
            case FREELANCE:
                return "🎯";
            case INTERNSHIP:
                return "🎓";
            case APPRENTICESHIP:
                return "🔧";
            case VOLUNTEER:
                return "❤️";
            default:
                return "💼";
        }
    }
}
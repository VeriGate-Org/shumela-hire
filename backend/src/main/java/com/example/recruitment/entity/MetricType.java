package com.example.recruitment.entity;

public enum MetricType {
    COUNT("Count", "Absolute number/count metric"),
    PERCENTAGE("Percentage", "Percentage-based metric"),
    RATIO("Ratio", "Ratio between two values"),
    RATE("Rate", "Rate over time"),
    DURATION("Duration", "Time duration metric"),
    COST("Cost", "Financial cost metric"),
    EFFICIENCY("Efficiency", "Efficiency measurement"),
    QUALITY("Quality", "Quality score metric"),
    VOLUME("Volume", "Volume-based metric"),
    CONVERSION("Conversion", "Conversion rate metric");

    private final String displayName;
    private final String description;

    MetricType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultUnit() {
        switch (this) {
            case COUNT:
            case VOLUME:
                return "units";
            case PERCENTAGE:
            case CONVERSION:
                return "%";
            case RATIO:
                return "ratio";
            case RATE:
                return "per day";
            case DURATION:
                return "days";
            case COST:
                return "ZAR";
            case EFFICIENCY:
            case QUALITY:
                return "score";
            default:
                return "value";
        }
    }

    public boolean isHigherBetter() {
        switch (this) {
            case COUNT:
            case PERCENTAGE:
            case RATIO:
            case RATE:
            case EFFICIENCY:
            case QUALITY:
            case VOLUME:
            case CONVERSION:
                return true;
            case DURATION:
            case COST:
                return false;
            default:
                return true;
        }
    }
}
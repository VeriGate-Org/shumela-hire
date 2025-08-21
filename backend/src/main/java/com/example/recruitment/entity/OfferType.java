package com.example.recruitment.entity;

public enum OfferType {
    FULL_TIME_PERMANENT("Full-Time Permanent", "Standard permanent employment"),
    PART_TIME_PERMANENT("Part-Time Permanent", "Part-time permanent employment"),
    CONTRACT_FIXED_TERM("Fixed-Term Contract", "Contract with defined end date"),
    CONTRACT_RENEWABLE("Renewable Contract", "Contract with renewal options"),
    CONSULTANT("Consultant", "Independent contractor arrangement"),
    INTERNSHIP("Internship", "Temporary learning position"),
    APPRENTICESHIP("Apprenticeship", "Formal training program"),
    TEMPORARY("Temporary", "Short-term temporary position"),
    PROBATIONARY("Probationary", "Initial probationary period"),
    EXECUTIVE("Executive", "Senior executive position");

    private final String displayName;
    private final String description;

    OfferType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresContractEndDate() {
        return this == CONTRACT_FIXED_TERM || this == INTERNSHIP || 
               this == APPRENTICESHIP || this == TEMPORARY;
    }

    public boolean allowsBenefits() {
        return this == FULL_TIME_PERMANENT || this == PART_TIME_PERMANENT || 
               this == EXECUTIVE || this == PROBATIONARY;
    }

    public boolean requiresEquityTerms() {
        return this == EXECUTIVE;
    }

    public boolean hasRenewalOptions() {
        return this == CONTRACT_RENEWABLE || this == APPRENTICESHIP;
    }

    public int getDefaultProbationaryPeriodDays() {
        switch (this) {
            case FULL_TIME_PERMANENT:
            case PART_TIME_PERMANENT:
            case PROBATIONARY:
                return 90;
            case EXECUTIVE:
                return 180;
            default:
                return 0;
        }
    }
}
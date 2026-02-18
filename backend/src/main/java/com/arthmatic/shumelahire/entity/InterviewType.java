package com.arthmatic.shumelahire.entity;

public enum InterviewType {
    PHONE("Phone Interview"),
    VIDEO("Video Interview"),
    IN_PERSON("In-Person Interview"),
    PANEL("Panel Interview"),
    TECHNICAL("Technical Interview"),
    BEHAVIOURAL("Behavioural Interview"),
    COMPETENCY("Competency Interview"),
    GROUP("Group Interview"),
    PRESENTATION("Presentation Interview"),
    CASE_STUDY("Case Study Interview");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRemote() {
        return this == PHONE || this == VIDEO;
    }

    public boolean requiresLocation() {
        return this == IN_PERSON || this == PANEL || this == GROUP || this == PRESENTATION;
    }

    public boolean requiresMeetingRoom() {
        return this == PANEL || this == GROUP || this == PRESENTATION;
    }
}
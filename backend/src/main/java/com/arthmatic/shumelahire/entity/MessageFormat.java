package com.arthmatic.shumelahire.entity;

public enum MessageFormat {
    TEXT("Plain Text", "Plain text message"),
    HTML("HTML", "HTML formatted message"),
    MARKDOWN("Markdown", "Markdown formatted message"),
    RICH_TEXT("Rich Text", "Rich text formatted message");

    private final String displayName;
    private final String description;

    MessageFormat(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean supportsFormatting() {
        return this != TEXT;
    }

    public boolean supportsLinks() {
        return this == HTML || this == MARKDOWN || this == RICH_TEXT;
    }

    public boolean supportsImages() {
        return this == HTML || this == RICH_TEXT;
    }

    public String getContentType() {
        switch (this) {
            case HTML: return "text/html";
            case MARKDOWN: return "text/markdown";
            case RICH_TEXT: return "text/rich";
            default: return "text/plain";
        }
    }
}
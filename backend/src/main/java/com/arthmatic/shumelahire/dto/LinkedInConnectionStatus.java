package com.arthmatic.shumelahire.dto;

import java.time.LocalDateTime;

public class LinkedInConnectionStatus {

    private boolean connected;
    private String organizationName;
    private String organizationId;
    private LocalDateTime connectedAt;
    private boolean tokenExpired;

    public LinkedInConnectionStatus() {}

    public LinkedInConnectionStatus(boolean connected, String organizationName, String organizationId,
                                     LocalDateTime connectedAt, boolean tokenExpired) {
        this.connected = connected;
        this.organizationName = organizationName;
        this.organizationId = organizationId;
        this.connectedAt = connectedAt;
        this.tokenExpired = tokenExpired;
    }

    public static LinkedInConnectionStatus disconnected() {
        return new LinkedInConnectionStatus(false, null, null, null, false);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }

    public boolean isTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }
}

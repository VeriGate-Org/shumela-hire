package com.arthmatic.shumelahire.dto;

public class AdUserDto {

    private String adObjectId;
    private String displayName;
    private String email;
    private String jobTitle;
    private String department;

    public AdUserDto() {}

    public AdUserDto(String adObjectId, String displayName, String email, String jobTitle, String department) {
        this.adObjectId = adObjectId;
        this.displayName = displayName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.department = department;
    }

    public String getAdObjectId() { return adObjectId; }
    public void setAdObjectId(String adObjectId) { this.adObjectId = adObjectId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}

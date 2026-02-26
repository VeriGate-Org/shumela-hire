package com.arthmatic.shumelahire.dto;

public class LinkedInPostResponse {

    private boolean success;
    private String postUrl;
    private String message;

    public LinkedInPostResponse() {}

    public LinkedInPostResponse(boolean success, String postUrl, String message) {
        this.success = success;
        this.postUrl = postUrl;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

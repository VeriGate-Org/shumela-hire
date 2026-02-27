package com.arthmatic.shumelahire.dto.ad;

import jakarta.validation.constraints.NotBlank;

public class ADLoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String domain;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}

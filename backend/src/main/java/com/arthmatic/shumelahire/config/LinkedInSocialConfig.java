package com.arthmatic.shumelahire.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "linkedin.social.enabled", havingValue = "true")
public class LinkedInSocialConfig {

    @Value("${linkedin.social.client-id}")
    private String clientId;

    @Value("${linkedin.social.client-secret}")
    private String clientSecret;

    @Value("${linkedin.social.redirect-uri}")
    private String redirectUri;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}

package com.arthmatic.shumelahire.exception;

public class FeatureNotEnabledException extends RuntimeException {

    private final String featureCode;

    public FeatureNotEnabledException(String featureCode) {
        super("Feature not enabled: " + featureCode);
        this.featureCode = featureCode;
    }

    public String getFeatureCode() {
        return featureCode;
    }
}

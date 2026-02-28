package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.service.FeatureGateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

    private final FeatureGateService featureGateService;

    public FeatureController(FeatureGateService featureGateService) {
        this.featureGateService = featureGateService;
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<String>> getEnabledFeatures() {
        return ResponseEntity.ok(featureGateService.getEnabledFeatures());
    }
}

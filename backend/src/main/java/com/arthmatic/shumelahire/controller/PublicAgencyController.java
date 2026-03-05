package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.AgencyRegistrationRequest;
import com.arthmatic.shumelahire.service.AgencyRegistrationService;
import com.arthmatic.shumelahire.service.AgencyRegistrationService.RegistrationResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/agencies")
public class PublicAgencyController {

    private static final Logger log = LoggerFactory.getLogger(PublicAgencyController.class);

    private final AgencyRegistrationService registrationService;

    public PublicAgencyController(AgencyRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAgency(@Valid @RequestBody AgencyRegistrationRequest request) {
        try {
            RegistrationResult result = registrationService.register(request);

            if (result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("success", true, "message", result.getMessage()));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", result.getMessage()));

        } catch (Exception e) {
            log.error("Agency registration error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Registration failed. Please try again."));
        }
    }
}

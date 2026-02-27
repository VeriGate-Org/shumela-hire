package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.JwtResponse;
import com.arthmatic.shumelahire.dto.ad.ADLoginRequest;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.security.JwtUtil;
import com.arthmatic.shumelahire.service.ad.ActiveDirectoryAuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/ad")
@ConditionalOnBean(ActiveDirectoryAuthService.class)
public class ADAuthController {

    private static final Logger logger = LoggerFactory.getLogger(ADAuthController.class);

    private final ActiveDirectoryAuthService adAuthService;
    private final JwtUtil jwtUtil;

    public ADAuthController(ActiveDirectoryAuthService adAuthService, JwtUtil jwtUtil) {
        this.adAuthService = adAuthService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> adLogin(@Valid @RequestBody ADLoginRequest request) {
        try {
            User user = adAuthService.authenticate(request.getUsername(), request.getPassword());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
            String token = jwtUtil.generateJwtToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            JwtResponse response = new JwtResponse(
                    token,
                    refreshToken,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name(),
                    user.getAuthorities().stream()
                            .map(a -> a.getAuthority())
                            .collect(Collectors.toList()),
                    user.isTwoFactorEnabled(),
                    user.isEmailVerified(),
                    jwtUtil.getRemainingTime(token)
            );

            logger.info("AD login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("AD login failed for user: {}", request.getUsername());
            return ResponseEntity.status(401)
                    .body(java.util.Map.of("error", "Active Directory authentication failed",
                            "message", "Invalid credentials or AD server unavailable"));
        }
    }

    @GetMapping("/sso")
    public ResponseEntity<?> ssoLogin() {
        // SPNEGO/Kerberos SSO — the user identity is resolved from the Kerberos ticket
        // by the SPNEGO filter in the security chain. If we reach this endpoint,
        // the user is already authenticated via Kerberos.
        // For now, return a placeholder response indicating SSO is available.
        return ResponseEntity.ok(java.util.Map.of(
                "ssoEnabled", true,
                "message", "SPNEGO/Kerberos SSO endpoint. " +
                        "Configure your browser for Integrated Windows Authentication."));
    }
}

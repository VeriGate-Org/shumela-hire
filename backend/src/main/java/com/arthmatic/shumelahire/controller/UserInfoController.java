package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Returns current user information from the security context.
 * Works with both dev JWT and Cognito JWT authentication.
 */
@RestController
@RequestMapping("/api/auth")
public class UserInfoController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        userInfo.put("roles", roles);

        // If the principal is a Cognito JWT
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            userInfo.put("sub", jwt.getSubject());
            userInfo.put("email", jwt.getClaimAsString("email"));
            userInfo.put("username", jwt.getClaimAsString("cognito:username"));
            userInfo.put("name", jwt.getClaimAsString("name"));

            // Try to find the user in our database by email
            String email = jwt.getClaimAsString("email");
            if (email != null) {
                Optional<User> user = userRepository.findByEmail(email);
                user.ifPresent(u -> {
                    userInfo.put("id", u.getId());
                    userInfo.put("firstName", u.getFirstName());
                    userInfo.put("lastName", u.getLastName());
                    userInfo.put("role", u.getRole().name());
                });
            }
        }
        // If the principal is a UserDetails (dev profile)
        else if (authentication.getPrincipal() instanceof User user) {
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("role", user.getRole().name());
        } else {
            userInfo.put("principal", authentication.getName());
        }

        return ResponseEntity.ok(userInfo);
    }
}

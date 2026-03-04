package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.AdUserDto;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.service.integration.AzureAdUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserSearchController {

    @Autowired(required = false)
    private AzureAdUserService azureAdUserService;

    @GetMapping("/search-ad")
    public ResponseEntity<?> searchAdUsers(@RequestParam("q") String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query must be at least 2 characters"));
        }

        if (azureAdUserService == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<AdUserDto> results = azureAdUserService.searchUsers(query.trim());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/provision-ad")
    public ResponseEntity<?> provisionAdUser(@RequestBody AdUserDto adUser) {
        if (adUser.getEmail() == null || adUser.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        if (azureAdUserService == null) {
            return ResponseEntity.status(503).body(Map.of("error", "Azure AD integration is not enabled"));
        }

        User user = azureAdUserService.provisionUser(adUser);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("name", ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                (user.getLastName() != null ? user.getLastName() : "")).trim());
        result.put("email", user.getEmail());
        result.put("role", user.getRole().getDisplayName());

        return ResponseEntity.ok(result);
    }
}

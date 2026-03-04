package com.arthmatic.shumelahire.service.integration;

import com.arthmatic.shumelahire.dto.AdUserDto;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.UserRepository;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "microsoft.enabled", havingValue = "true")
public class AzureAdUserService {

    private static final Logger logger = LoggerFactory.getLogger(AzureAdUserService.class);

    private final GraphServiceClient graphClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AzureAdUserService(GraphServiceClient graphClient, UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.graphClient = graphClient;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdUserDto> searchUsers(String query) {
        try {
            UserCollectionResponse response = graphClient.users().get(requestConfig -> {
                requestConfig.queryParameters.search =
                        "\"displayName:" + query + "\" OR \"mail:" + query + "\"";
                requestConfig.queryParameters.select =
                        new String[]{"id", "displayName", "mail", "jobTitle", "department"};
                requestConfig.queryParameters.top = 20;
                requestConfig.headers.add("ConsistencyLevel", "eventual");
            });

            if (response == null || response.getValue() == null) {
                return Collections.emptyList();
            }

            return response.getValue().stream()
                    .map(u -> new AdUserDto(
                            u.getId(),
                            u.getDisplayName(),
                            u.getMail(),
                            u.getJobTitle(),
                            u.getDepartment()
                    ))
                    .toList();
        } catch (Exception e) {
            logger.error("Failed to search Azure AD users: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public User provisionUser(AdUserDto adUser) {
        // Check if user already exists by email
        if (adUser.getEmail() != null) {
            var existing = userRepository.findByEmail(adUser.getEmail());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Create new user with INTERVIEWER role
        String[] nameParts = splitDisplayName(adUser.getDisplayName());
        String username = generateUsername(adUser.getEmail(), adUser.getDisplayName());

        User user = new User();
        user.setUsername(username);
        user.setEmail(adUser.getEmail());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts[1]);
        user.setRole(User.Role.INTERVIEWER);
        user.setSsoProvider("AZURE_AD");
        user.setSsoUserId(adUser.getAdObjectId());
        user.setEnabled(true);
        user.setJobTitle(adUser.getJobTitle());
        user.setDepartment(adUser.getDepartment());

        return userRepository.save(user);
    }

    private String[] splitDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return new String[]{"", ""};
        }
        String[] parts = displayName.trim().split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }

    private String generateUsername(String email, String displayName) {
        if (email != null && email.contains("@")) {
            String base = email.substring(0, email.indexOf('@')).toLowerCase();
            if (!userRepository.existsByUsername(base)) {
                return base;
            }
            return base + "_" + System.currentTimeMillis() % 10000;
        }
        String base = (displayName != null ? displayName : "user").toLowerCase().replaceAll("\\s+", ".");
        if (!userRepository.existsByUsername(base)) {
            return base;
        }
        return base + "_" + System.currentTimeMillis() % 10000;
    }
}

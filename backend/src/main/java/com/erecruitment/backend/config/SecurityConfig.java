package com.erecruitment.backend.config;

import com.erecruitment.backend.security.JwtAuthenticationFilter;
import com.erecruitment.backend.security.JwtAuthenticationEntryPoint;
import com.erecruitment.backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the e-recruitment system
 * Implements JWT-based authentication with role-based access control
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password encoder using BCrypt with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication provider using custom user details service
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS configuration for cross-origin requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "https://*.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints
            .csrf(AbstractHttpConfigurer::disable)
            
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure exception handling
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // Set session management to stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/audit/**").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers("/api/users/manage/**").hasAnyRole("ADMIN", "HR_MANAGER")
                
                // HR Manager endpoints
                .requestMatchers("/api/requisitions/approve/**").hasAnyRole("ADMIN", "HR_MANAGER", "HIRING_MANAGER")
                .requestMatchers("/api/job-postings/publish/**").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers("/api/analytics/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")
                
                // Recruiter endpoints
                .requestMatchers("/api/applications/manage/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")
                .requestMatchers("/api/interviews/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER", "HIRING_MANAGER")
                
                // General authenticated endpoints
                .requestMatchers("/api/**").authenticated()
                
                // Permit all other requests
                .anyRequest().permitAll()
            );

        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Set authentication provider
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}

package com.arthmatic.shumelahire.config;

import com.arthmatic.shumelahire.config.tenant.TenantResolutionFilter;
import com.arthmatic.shumelahire.repository.TenantRepository;
import com.arthmatic.shumelahire.security.JwtAuthenticationEntryPoint;
import com.arthmatic.shumelahire.security.JwtAuthenticationFilter;
import com.arthmatic.shumelahire.security.RateLimitFilter;
import com.arthmatic.shumelahire.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration for Active Directory / on-premises deployments.
 * Uses JWT for API authentication with AD LDAP as the identity provider.
 * Falls back to local DB authentication when AD is not configured.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Profile({"sqlserver", "onprem"})
public class ActiveDirectorySecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://*.shumelahire.co.za"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/me").authenticated()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Actuator
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // Admin endpoints (includes AD admin)
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers("/api/audit/**").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers("/api/users/manage/**").hasAnyRole("ADMIN", "HR_MANAGER")

                // Executive endpoints
                .requestMatchers("/api/executive/**").hasAnyRole("ADMIN", "EXECUTIVE")

                // HR Manager endpoints
                .requestMatchers("/api/requisitions/approve/**").hasAnyRole("ADMIN", "HR_MANAGER", "HIRING_MANAGER")
                .requestMatchers("/api/job-postings/publish/**").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers("/api/analytics/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER", "EXECUTIVE")

                // Interview endpoints
                .requestMatchers("/api/interviews/assigned/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER", "HIRING_MANAGER", "INTERVIEWER")
                .requestMatchers("/api/interviews/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER", "HIRING_MANAGER", "INTERVIEWER")

                // Recruiter endpoints
                .requestMatchers("/api/applications/manage/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Internal jobs
                .requestMatchers("/api/internal/jobs/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER", "HIRING_MANAGER", "INTERVIEWER", "EMPLOYEE", "EXECUTIVE")

                // E-Signature endpoints
                .requestMatchers("/api/esignature/webhook").permitAll()
                .requestMatchers("/api/esignature/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Integration endpoints
                .requestMatchers("/api/integrations/ms-teams/webhook").permitAll()
                .requestMatchers("/api/integrations/**").hasAnyRole("ADMIN", "HR_MANAGER")

                // Agency endpoints
                .requestMatchers("/api/agencies/register").hasAnyRole("ADMIN", "HR_MANAGER")
                .requestMatchers("/api/agencies/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // AI endpoints
                .requestMatchers("/api/ai/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER", "HIRING_MANAGER", "INTERVIEWER")

                // LinkedIn Social endpoints
                .requestMatchers("/api/linkedin/social/auth/callback").permitAll()
                .requestMatchers("/api/linkedin/social/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Job board endpoints
                .requestMatchers("/api/job-boards/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Salary recommendation endpoints
                .requestMatchers("/api/salary-recommendations/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Vacancy report endpoints
                .requestMatchers("/api/vacancy-reports/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Shortlisting endpoints
                .requestMatchers("/api/shortlisting/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Talent pool endpoints
                .requestMatchers("/api/talent-pools/**").hasAnyRole("ADMIN", "HR_MANAGER", "RECRUITER")

                // Compliance endpoints
                .requestMatchers("/api/compliance/**").hasAnyRole("ADMIN", "HR_MANAGER", "EXECUTIVE")

                // General authenticated endpoints
                .requestMatchers("/api/**").authenticated()

                .anyRequest().denyAll()
            );

        http.addFilterBefore(new TenantResolutionFilter(tenantRepository, environment), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    /**
     * LDAP context source — conditionally created when AD is enabled.
     */
    @Bean
    @ConditionalOnProperty(name = "shumelahire.ad.enabled", havingValue = "true")
    public LdapContextSource ldapContextSource(
            @Value("${shumelahire.ad.url}") String url,
            @Value("${shumelahire.ad.base-dn}") String baseDn,
            @Value("${shumelahire.ad.bind-dn:}") String bindDn,
            @Value("${shumelahire.ad.bind-password:}") String bindPassword) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(url);
        contextSource.setBase(baseDn);
        if (!bindDn.isEmpty()) {
            contextSource.setUserDn(bindDn);
            contextSource.setPassword(bindPassword);
        }
        contextSource.setReferral("follow");
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    @ConditionalOnProperty(name = "shumelahire.ad.enabled", havingValue = "true")
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}

package com.optimal.loyalty.Optimal.Loyalty.config;

import com.optimal.loyalty.Optimal.Loyalty.repository.UserRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider; // Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Autowired // Inject the filter
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired // Inject the provider (defined in ApplicationConfig)
    private AuthenticationProvider authenticationProvider;

    @Autowired // Inject CustomOAuth2UserService if needed by filter/provider
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Consider enabling CSRF properly later
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers( // Public endpoints
                                "/api/users/login",
                                "/api/users/signup",
                                "/api/users/reset-password-request",
                                "/api/users/reset-password",
                                "/api/users/validate", // Keep validate public for frontend check? Or protect it too?
                                "/api/users/google", // Handled by oauth2Login
                                "/oauth2/**",
                                "/api/users/validate"
                                //"api/cart/add"
                                // Handled by oauth2Login
                        ).permitAll()
                        .anyRequest().authenticated() // All other requests need authentication
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Correct for JWT
                .authenticationProvider(authenticationProvider) // Tell Spring Security about the provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT Filter BEFORE the standard username/password filter
                .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService) // Use the bean directly
                                )
                                .successHandler(oauth2LoginSuccessHandler)
                        // Optional: Configure failure handler, authorization endpoint base URI if needed
                        // .authorizationEndpoint(authz -> authz.baseUri("/oauth2/authorization"))
                        // .redirectionEndpoint(redir -> redir.baseUri("/oauth2/callback/*"))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true) // Less relevant for stateless
                        .clearAuthentication(true)
                        .deleteCookies("loginToken") // Good
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            // Optionally clear other cookies if needed
                            response.getWriter().write("Logout successful");
                        })
                );


        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow only your frontend origin in production
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // Be specific
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With")); // Be specific
        config.setAllowCredentials(true);
        // Expose Set-Cookie for login, potentially others if needed by frontend JS
        config.setExposedHeaders(Arrays.asList("Set-Cookie"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS to all paths
        return source;
    }

    // CustomOAuth2UserService bean is likely still needed if you use OAuth2
    // Remove the @Bean definition here if it's already defined elsewhere or autowired
    // @Bean
    // public CustomOAuth2UserService customOAuth2UserService() {
    //     // Assuming it's already a @Service or defined elsewhere
    //     return customOAuth2UserService; // Return the autowired instance
    // }

    // Remove UserDetailsService, PasswordEncoder, AuthenticationProvider beans from here
    // if you moved them to ApplicationConfig
}
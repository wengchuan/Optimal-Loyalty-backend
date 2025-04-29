package com.optimal.loyalty.Optimal.Loyalty.config; // Or a suitable package

import com.optimal.loyalty.Optimal.Loyalty.service.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils; // For easy cookie finding

import java.io.IOException;

@Component
@RequiredArgsConstructor // Lombok constructor injection
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtService;
    private final UserDetailsService userDetailsService; // Inject your UserDetailsService

    @Autowired
    public JwtAuthenticationFilter(JwtServiceImpl jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String jwt;
        final String userEmail;

        // 1. Try to extract JWT from Cookie
        Cookie jwtCookie = WebUtils.getCookie(request, "loginToken");
        if (jwtCookie == null || jwtCookie.getValue().isEmpty()) {
            filterChain.doFilter(request, response); // No token, continue chain
            return;
        }
        jwt = jwtCookie.getValue();

        //TODO : error here prevent new google account login fix it
        try {
            userEmail = jwtService.extractUsername(jwt); // Extract email from token

            // 2. Check if user is not already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 3. Load user details
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 4. Validate token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // 5. Create Authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials (usually null for JWT)
                            userDetails.getAuthorities()
                    );
                    System.out.println("user details "+userDetails);
                    System.out.println("user details "+userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // 6. Update SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log error if needed, e.g., token expired, malformed etc.
            logger.warn("JWT token processing error: {}" + e.getMessage());
            // Optionally clear context if invalid token found, depending on desired behavior
            // SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(request, response); // Continue filter chain
    }
}
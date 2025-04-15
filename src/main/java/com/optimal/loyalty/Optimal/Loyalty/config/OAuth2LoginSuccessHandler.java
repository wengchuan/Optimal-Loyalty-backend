package com.optimal.loyalty.Optimal.Loyalty.config;

import com.optimal.loyalty.Optimal.Loyalty.service.JwtService;
import com.optimal.loyalty.Optimal.Loyalty.service.UserService;
import com.optimal.loyalty.Optimal.Loyalty.dto.CreateUserDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        System.out.println("OAuth2 success handler called");
        System.out.println("email: " + email);
        System.out.println("name: " + name);

        // Save user if not already registered
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isEmpty()) {
            String randomPassword = UUID.randomUUID().toString();
            CreateUserDTO newUser = new CreateUserDTO(name, randomPassword, email);
            existingUser = Optional.ofNullable(userService.createUser(newUser));
        }

        // Generate JWT token
        String token = jwtService.generateToken(email);

        // Create HTTP-only cookie
        ResponseCookie jwtCookie = ResponseCookie.from("loginToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 24 hours
                .sameSite("Strict")
                .build();

        System.out.println("Setting cookie: " + jwtCookie.toString());

        // Add cookie to response
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // Redirect to frontend with token in query params for initial authentication
        // The token in the cookie will be used for subsequent requests
        getRedirectStrategy().sendRedirect(request, response,
                "http://localhost:3000/home");
    }
}
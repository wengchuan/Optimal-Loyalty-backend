package com.optimal.loyalty.Optimal.Loyalty.controller;


import com.optimal.loyalty.Optimal.Loyalty.dto.CreateUserDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserDetailsUpdateDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.UserLoginDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.PasswordRequest;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.response.GenericResponse;
import com.optimal.loyalty.Optimal.Loyalty.response.LoginResponse;
import com.optimal.loyalty.Optimal.Loyalty.service.AuthenticateService;
import com.optimal.loyalty.Optimal.Loyalty.service.EmailService;
import com.optimal.loyalty.Optimal.Loyalty.service.UserService;
import com.optimal.loyalty.Optimal.Loyalty.service.impl.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticateService authenticateService;
    private final JwtServiceImpl jwt;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, AuthenticateService authenticateService, JwtServiceImpl jwt, EmailService emailService) {
        this.userService = userService;
        this.authenticateService = authenticateService;
        this.jwt = jwt;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO createUserDTO){
        User createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody UserLoginDTO userLoginDTO){
        UserDTO authenticateUser = authenticateService.authenticate(userLoginDTO);
        String token = jwt.generateToken(authenticateUser.getEmail());

        ResponseCookie jwtCookie = ResponseCookie.from("loginToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 24 hours
                .sameSite("Lax")
                .build();


        return  ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new LoginResponse(authenticateUser.getUsername(),authenticateUser.getEmail()));
    }

@GetMapping("/points")
public ResponseEntity<Integer> getUserPoints() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String email = authentication.getName();
    Optional<User> user = userService.findByEmail(email);
    if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    int points = user.get().getPoints();
    return ResponseEntity.ok(points);
}


    @GetMapping("/google")
    public ResponseEntity<?> googleLogin(@AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        System.out.println("email :"+email);
        System.out.println("name :"+name);

        // Optional: Save the user in the database if not already registered
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isEmpty()) {
            String randomPassword = UUID.randomUUID().toString();
            CreateUserDTO newUser = new CreateUserDTO(name,randomPassword, email); // Use random password for now
            existingUser = Optional.ofNullable(userService.createUser(newUser));
        }

        // Generate JWT token
        String token = jwt.generateToken(email);

        // Create HTTP-only cookie
        ResponseCookie jwtCookie = ResponseCookie.from("loginToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 24 hours
                .sameSite("Lax")
                .build();

        // Send back response with token and user info
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new LoginResponse( name, email ));
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        try {
            // Extract email from payload
            String email = payload.get("email");

            // Validate email
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            Optional<User> user = userService.findByEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("No user found with this email");
            }

            String token = userService.generateResetToken(email);

            // Construct reset URL
            String contextPath = request.getScheme() + "://" +
                    request.getServerName() +
                    (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");

            try {
                emailService.sendResetPasswordEmail(contextPath, request.getLocale(), token, user.get());
                return ResponseEntity.ok("Password reset link sent successfully");
            } catch (Exception e) {
                // Log the email sending error
                System.err.println("Failed to send reset email: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send reset email");
            }
        } catch (Exception e) {
            // Catch any unexpected errors
            System.err.println("Unexpected error in password reset request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@CookieValue(name = "loginToken", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("authenticated", false);
            response.put("message", "No token provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        System.out.println("Extracted Token: " + token); // Debug log

        try {
            // Extract username
            String username = jwt.extractUsername(token);
            System.out.println("Extracted Username: " + username); // Debug log

            // Check if user exists
            Optional<User> user = userService.findByEmail(username);

            // Check if user is present
            User userDetails = null;
            UserDetails userDetailsObj = null;
            if (user.isPresent()) {
                userDetails = user.get();
                userDetailsObj = new org.springframework.security.core.userdetails.User(
                        userDetails.getEmail(), userDetails.getPassword(), new ArrayList<>()
                );
            }

//            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                    user.getUsername(), user.getPassword(), new ArrayList<>()
//            );


            // Validate token
            System.out.println("User Detail Obj : "+ userDetailsObj);
            boolean isValid = jwt.validateToken(token, userDetailsObj);
            System.out.println("Token Validation Result: " + isValid); // Debug log

            if (isValid) {
                response.put("authenticated", true);
                response.put("message", "Token is valid");
                return ResponseEntity.ok(response);
            } else {
                response.put("authenticated", false);
                response.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (UsernameNotFoundException e) {
            response.put("authenticated", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("authenticated", false);
            response.put("message", "Token validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getPassword());
            return ResponseEntity.ok("Password reset successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //TODO : RETURN USER ADDESS AND EMAIL & other detail
    @PostMapping("/user_info")
    public ResponseEntity<User> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);

        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            User user1 = user.get();
        return ResponseEntity.ok(user1);
    }
    }
    @PostMapping("/update")
    public ResponseEntity<Void> updateCustomer(@RequestBody UserDetailsUpdateDTO userDetailsUpdateDTO) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);

        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

         User user1 = user.get();

        // Check each field in UserDetailsUpdateDTO and update the user object if not null
        if (userDetailsUpdateDTO.getUsername() != null && !userDetailsUpdateDTO.getUsername().isEmpty()) {
            user1.setUsername(userDetailsUpdateDTO.getUsername());
        }
        if (userDetailsUpdateDTO.getEmail() != null && !userDetailsUpdateDTO.getEmail().isEmpty()) {
            user1.setEmail(userDetailsUpdateDTO.getEmail());
        }
        if (userDetailsUpdateDTO.getPassword() != null && !userDetailsUpdateDTO.getPassword().isEmpty()) {
            user1.setPassword(userDetailsUpdateDTO.getPassword());
        }
        if (userDetailsUpdateDTO.getPhoneNumber() != null && !userDetailsUpdateDTO.getPhoneNumber().isEmpty()) {
            user1.setPhoneNumber(userDetailsUpdateDTO.getPhoneNumber());
        }
        if (userDetailsUpdateDTO.getAddress() != null && !userDetailsUpdateDTO.getAddress().isEmpty()) {
            user1.setAddress(userDetailsUpdateDTO.getAddress());
        }
        if (userDetailsUpdateDTO.getAboutMe() != null && !userDetailsUpdateDTO.getAboutMe().isEmpty()) {
            user1.setAboutMe(userDetailsUpdateDTO.getAboutMe());
        }

        // Save the updated user object
        userService.saveUser(user1);

        return ResponseEntity.ok().build();

    }
}

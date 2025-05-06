package com.optimal.loyalty.Optimal.Loyalty.controller;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItemHistory;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.service.CheckoutService;
import com.optimal.loyalty.Optimal.Loyalty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserService userService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, UserService userService) {
        
        this.checkoutService = checkoutService;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<List<CartItemHistory>> checkOut(@RequestBody List<CartItem> cartItemList) {
      List<CartItemHistory> cartItemHistories = checkoutService.checkOut(cartItemList);
     
        return ResponseEntity.ok(cartItemHistories);
    }

   @PostMapping("/cart_history")
    public ResponseEntity<List<CartItemHistory>> getCartItemHistory() {
        // Get the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // Return HTTP 401 Unauthorized if the user is not authenticated
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract the authenticated user's email
        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);

        // Retrieve the user from the database using userService
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            // Return HTTP 401 Unauthorized if the user is not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Fetch the cart item history for the user
        List<CartItemHistory> cartItemHistories = checkoutService.getCartItemHistory(user.get().getId());

        // Return the history as a ResponseEntity
        return ResponseEntity.ok(cartItemHistories);
    }


    
}
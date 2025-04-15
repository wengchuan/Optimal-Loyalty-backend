package com.optimal.loyalty.Optimal.Loyalty.controller;


import com.optimal.loyalty.Optimal.Loyalty.dto.AddToCartDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.DeleteCartItemDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.service.CartService;
import com.optimal.loyalty.Optimal.Loyalty.service.UserService;
import com.optimal.loyalty.Optimal.Loyalty.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final JwtServiceImpl jwt;
    private final UserService userService;
    List<CartItem> cartItems = null;

    @Autowired
    public CartController(CartService cartService, JwtServiceImpl jwt, UserService userService) {
        this.cartService = cartService;
        this.jwt = jwt;
        this.userService = userService;
    }
    @GetMapping("/")
    public ResponseEntity<List<CartItem>> getCartItemsWithVoucherDetails() {
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



        cartItems = cartService.getAllCartItemByUserId(user.get().getId());

        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody AddToCartDTO addToCartDTO) {
        try {
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

            CartItem cartItem = cartService.addToCart(
                    user.get().getId(),
                    addToCartDTO.getVoucherId(),
                    addToCartDTO.getQuantity()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<CartItem> updateCart(@RequestBody AddToCartDTO addToCartDTO) {
        try {
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

            CartItem cartItem = cartService.updateCart(
                    user.get().getId(),
                    addToCartDTO.getVoucherId(),
                    addToCartDTO.getQuantity()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCartItem(@RequestBody DeleteCartItemDTO deleteCartItemDTO) {
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

        boolean deleted = cartService.deleteCartItem(deleteCartItemDTO.getId());
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404 if not found
        }
    }


}

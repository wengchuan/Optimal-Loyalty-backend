package com.optimal.loyalty.Optimal.Loyalty.controller;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItemHistory;
import com.optimal.loyalty.Optimal.Loyalty.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public List<CartItemHistory> checkOut(@RequestBody List<CartItem> cartItemList) {
        return checkoutService.checkOut(cartItemList);
    }
}
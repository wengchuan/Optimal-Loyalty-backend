package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItemHistory;

import java.util.List;

public interface CheckoutService {
    List<CartItemHistory> checkOut(List<CartItem> cartItemList);
    List<CartItemHistory> getCartItemHistory (int userId);
}

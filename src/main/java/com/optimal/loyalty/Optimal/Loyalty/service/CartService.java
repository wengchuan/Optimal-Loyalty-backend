package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;

import java.util.List;

public interface CartService{
    List<CartItem> getAllCartItemByUserId(int userId);
    CartItem addToCart(int userId, int voucherId, int quantity);
    CartItem updateCart(int userId, int voucherId, int quantity);
    boolean deleteCartItem(int voucherId);
}

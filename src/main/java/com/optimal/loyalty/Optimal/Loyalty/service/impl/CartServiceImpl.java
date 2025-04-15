package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.repository.CartRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    @Override
    public List<CartItem> getAllCartItemByUserId(int userId) {


        return cartRepository.findCartItemsWithVoucherDetailsByUserId(userId);
    }

    @Override
    public CartItem addToCart(int userId, int voucherId, int quantity) {
        // Validate input
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Check if the user already has this voucher in their cart
        Optional<CartItem> existingCartItem = cartRepository.findByUserIdAndVoucherId(userId, voucherId);

        if (existingCartItem.isPresent()) {
            // Update existing cart item
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setVoucherId(voucherId);
            newCartItem.setQuantity(quantity);
            return cartRepository.save(newCartItem);
        }
    }

    @Override
    public CartItem updateCart(int userId, int voucherId, int quantity) {
        // Check if the user already has this voucher in their cart
        Optional<CartItem> existingCartItem = cartRepository.findByUserIdAndVoucherId(userId, voucherId);

        if (existingCartItem.isPresent()) {
            // Update existing cart item
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(quantity);
            return cartRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setVoucherId(voucherId);
            newCartItem.setQuantity(quantity);
            return cartRepository.save(newCartItem);
        }
    }
    @Override
    public boolean deleteCartItem(int cartItemId) {
        if (cartRepository.existsById(cartItemId)) {
            cartRepository.deleteById(cartItemId);
            return true;
        } else {
            return false;
        }
    }

}

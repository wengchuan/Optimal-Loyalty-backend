package com.optimal.loyalty.Optimal.Loyalty.service.impl;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItemHistory;
import com.optimal.loyalty.Optimal.Loyalty.service.CartService;
import com.optimal.loyalty.Optimal.Loyalty.service.CheckoutService;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CartService cartService;
    private final VoucherCodeService voucherCodeService;

    @Autowired
    public CheckoutServiceImpl(CartService cartService, VoucherCodeService voucherCodeService) {
        this.cartService = cartService;
        this.voucherCodeService = voucherCodeService;
    }

    @Override
    public List<CartItemHistory> checkOut(List<CartItem> cartItemList) {
        // Validate and process each cart item
        List<CartItemHistory> historyList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            // Verify the voucher for each cart item
//            boolean isVoucherValid = voucherCodeService.verifyVoucher(cartItem.getVoucherCode());
//            if (!isVoucherValid) {
//                throw new IllegalArgumentException("Invalid or already used voucher for code: " + cartItem.getVoucherCode());
//            }
//
//            // Mark the voucher as used
//            boolean isVoucherRedeemed = voucherCodeService.redeemVoucher(cartItem.getVoucherCode());
//            if (!isVoucherRedeemed) {
//                throw new IllegalStateException("Failed to redeem the voucher for code: " + cartItem.getVoucherCode());
//            }

            // Create CartItemHistory for the current cart item
            CartItemHistory history = new CartItemHistory();
            history.setUserId(cartItem.getUserId());
            history.setVoucherId(cartItem.getVoucherId());
            history.setQuantity(cartItem.getQuantity());
            history.setCompleted_date(LocalDateTime.now().toString());

            // Add to history list
            cartService.deleteCartItem(cartItem.getId());
            historyList.add(history);
        }

        return historyList;
    }
}
package com.optimal.loyalty.Optimal.Loyalty.service.impl;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import com.optimal.loyalty.Optimal.Loyalty.model.CartItemHistory;
import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;
import com.optimal.loyalty.Optimal.Loyalty.repository.CartItemHistoryRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.CartService;
import com.optimal.loyalty.Optimal.Loyalty.service.CheckoutService;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherCodeService;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CartService cartService;
    private final VoucherCodeService voucherCodeService;
    private final CartItemHistoryRepository cartItemHistoryRepository;
    private  final VoucherService voucherService;

    @Autowired
    public CheckoutServiceImpl(CartService cartService, VoucherCodeService voucherCodeService, CartItemHistoryRepository cartItemHistoryRepository, VoucherService voucherService) {
        this.cartService = cartService;
        this.voucherCodeService = voucherCodeService;
        this.cartItemHistoryRepository = cartItemHistoryRepository;
        this.voucherService = voucherService;
    }

    @Override
    public List<CartItemHistory> checkOut(List<CartItem> cartItemList) {
        CartItemHistory cartItemHistory = null;
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
            System.out.println("User id "+cartItem.getUserId());
            history.setVoucherId(cartItem.getVoucherId());
            history.setQuantity(cartItem.getQuantity());
            history.setCompleted_date(LocalDateTime.now().toString());

            // Add to history list
            cartService.deleteCartItem(cartItem.getId());
            cartItemHistory = cartItemHistoryRepository.save(history);
            Optional<Voucher> voucher = voucherService.getVoucherById(cartItem.getVoucherId());
            if(voucher.isEmpty()){
                return null; 
            }
            cartItemHistory.setVoucher(voucher.get());
            historyList.add(cartItemHistory);

        }

        return historyList;
    }

    @Override
     public List<CartItemHistory> getCartItemHistory (int userId){
          // Check if the userId is valid
        if (userId == 0) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // Fetch the cart item history for the user
        List<CartItemHistory> historyList = cartItemHistoryRepository.getCartItemHistoryByUserId(userId);

        // If no history is found, return an empty list
        if (historyList == null) {
            return new ArrayList<>();
        }

        return historyList;


     }
    
}
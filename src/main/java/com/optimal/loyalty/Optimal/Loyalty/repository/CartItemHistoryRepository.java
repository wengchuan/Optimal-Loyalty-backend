package com.optimal.loyalty.Optimal.Loyalty.repository;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemHistoryRepository extends JpaRepository<CartItemHistory,Integer> {
    List<CartItemHistory> getCartItemHistoryByUserId(int userId);
}

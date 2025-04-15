package com.optimal.loyalty.Optimal.Loyalty.repository;

import com.optimal.loyalty.Optimal.Loyalty.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.voucher WHERE ci.userId = :userId")
    List<CartItem> findCartItemsWithVoucherDetailsByUserId(@Param("userId") int userId);

    Optional<CartItem> findByUserIdAndVoucherId(int userId, int voucherId);
}

package com.optimal.loyalty.Optimal.Loyalty.repository;

import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Integer> {

    List<Voucher> findByTitleContainingIgnoreCase(String title);
    List<Voucher> findByCategoryId(int categoryId);

}

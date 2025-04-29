package com.optimal.loyalty.Optimal.Loyalty.repository;

import com.optimal.loyalty.Optimal.Loyalty.model.VoucherCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherCodeRepository extends JpaRepository<VoucherCode,Integer> {
    Optional<VoucherCode> findByCode(String code);

}

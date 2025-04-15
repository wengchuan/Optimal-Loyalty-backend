package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;

import java.util.List;
import java.util.Optional;


public interface VoucherService {
    List<Voucher> getAllVoucher ();
    List<Voucher> searchVoucher(String title);
    List<Voucher> getVouchersByCategory(int categoryId);
    Optional<Voucher> getVoucherById(int voucherId);
}

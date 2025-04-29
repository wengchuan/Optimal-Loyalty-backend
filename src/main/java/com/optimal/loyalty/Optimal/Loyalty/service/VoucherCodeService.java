package com.optimal.loyalty.Optimal.Loyalty.service;

import com.optimal.loyalty.Optimal.Loyalty.dto.VerifyCodeDTO;

public interface VoucherCodeService {

    public String generateVoucher(int id);
    public boolean verifyVoucher(VerifyCodeDTO v);
    public boolean redeemVoucher(String code);
}

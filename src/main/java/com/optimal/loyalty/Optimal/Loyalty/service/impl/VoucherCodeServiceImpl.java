package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.dto.VerifyCodeDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;
import com.optimal.loyalty.Optimal.Loyalty.model.VoucherCode;
import com.optimal.loyalty.Optimal.Loyalty.repository.VoucherCodeRepository;
import com.optimal.loyalty.Optimal.Loyalty.repository.VoucherRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class VoucherCodeServiceImpl implements VoucherCodeService {

    private final VoucherCodeRepository voucherCodeRepository;
    private final VoucherRepository voucherRepository;

    @Autowired
    public VoucherCodeServiceImpl(VoucherCodeRepository voucherCodeRepository, VoucherRepository voucherRepository) {
        this.voucherCodeRepository = voucherCodeRepository;
        this.voucherRepository = voucherRepository;
    }

    // Generate random voucher code
    public String generateVoucher(int voucherId) {
        String code = UUID.randomUUID().toString().toUpperCase();

        VoucherCode voucher = new VoucherCode();
        voucher.setVoucherId(voucherId);
        voucher.setCode(code);
        voucherCodeRepository.save(voucher);

        return code;
    }

    // Verify a voucher code
    public boolean verifyVoucher(VerifyCodeDTO verifyCodeDTO) {
        Optional<VoucherCode> voucherOpt = voucherCodeRepository.findByCode(verifyCodeDTO.getCode());
        Optional<Voucher> voucher = voucherRepository.findById(verifyCodeDTO.getVoucherId());

        return voucherOpt.isPresent() && voucher.isPresent() && (voucher.get().getId() == voucherOpt.get().getVoucherId());// Valid and not used
    }

    // (Optional) Mark voucher as used
    public boolean redeemVoucher(String code) {
        Optional<VoucherCode> voucherOpt = voucherCodeRepository.findByCode(code);
        if (voucherOpt.isPresent() && !voucherOpt.get().isUsed()) {
            VoucherCode voucher = voucherOpt.get();
            voucher.setUsed(true);
            voucherCodeRepository.save(voucher);
            return true;
        }
        return false;
    }
}

package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;
import com.optimal.loyalty.Optimal.Loyalty.repository.VoucherRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository){
        this.voucherRepository = voucherRepository;
    }

    @Override
    public List<Voucher> getAllVoucher() {

        return voucherRepository.findAll();
    }

    @Override
    public List<Voucher> searchVoucher(String title) {
        return voucherRepository.findByTitleContainingIgnoreCase(title);
    }


    @Override
    public List<Voucher> getVouchersByCategory(int categoryId) {
        return voucherRepository.findByCategoryId(categoryId);
    }

    @Override
    public Optional<Voucher> getVoucherById(int voucherId) {
        Optional<Voucher> voucher;

        voucher = voucherRepository.findById(voucherId);

        return voucher;
    }


}

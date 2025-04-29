package com.optimal.loyalty.Optimal.Loyalty.controller;



import com.optimal.loyalty.Optimal.Loyalty.dto.SearchVoucherDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.VoucherCategoryDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/voucher")
public class VoucherController {
    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService){
        this.voucherService = voucherService;
    }

    @PostMapping("/")
    public ResponseEntity<List<Voucher>> getAllVoucher(){
        List<Voucher> voucherList = voucherService.getAllVoucher();
        return ResponseEntity.ok(voucherList);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Voucher>> searchVoucher(@RequestBody SearchVoucherDTO searchVoucherDTO){

        List<Voucher> voucherList = voucherService.searchVoucher(searchVoucherDTO.getTitle());

        return ResponseEntity.ok(voucherList);

    }

    @PostMapping("/category")
    public ResponseEntity<List<Voucher>> getVouchersByCategory(@RequestBody VoucherCategoryDTO voucherCategoryDTO) {
        List<Voucher> vouchers = voucherService.getVouchersByCategory(voucherCategoryDTO.getCategoryId());
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/id/{id}")
    public  ResponseEntity<Optional<Voucher>> getVoucherById(@PathVariable("id") int id){
        Optional<Voucher> voucher = voucherService.getVoucherById(id);

        return ResponseEntity.ok(voucher);

    }





}

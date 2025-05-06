package com.optimal.loyalty.Optimal.Loyalty.controller;

import com.lowagie.text.DocumentException;
import com.optimal.loyalty.Optimal.Loyalty.dto.CreateVoucherCodeDTO;
import com.optimal.loyalty.Optimal.Loyalty.dto.VerifyCodeDTO;
import com.optimal.loyalty.Optimal.Loyalty.model.User;
import com.optimal.loyalty.Optimal.Loyalty.model.Voucher;
import com.optimal.loyalty.Optimal.Loyalty.repository.UserRepository;
import com.optimal.loyalty.Optimal.Loyalty.service.UserService;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherCodeService;
import com.optimal.loyalty.Optimal.Loyalty.service.VoucherService;
import org.eclipse.angus.mail.handlers.image_jpeg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@RestController
@RequestMapping("/api/voucherCode")
public class VoucherCodeController {

    private final VoucherCodeService voucherCodeService;
    private final UserService userService;
    private final VoucherService voucherService;
    private final UserRepository userRepository;


    @Autowired
    public VoucherCodeController(VoucherCodeService voucherCodeService, UserService userService, VoucherService voucherService, UserRepository userRepository) {
        this.voucherCodeService = voucherCodeService;
        this.userService = userService;
        this.voucherService = voucherService;
        this.userRepository = userRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateVoucher(@RequestBody CreateVoucherCodeDTO createVoucherCodeDTO) throws DocumentException {
        String code = voucherCodeService.generateVoucher(createVoucherCodeDTO.getVoucherId());
         Optional<Voucher> voucher = voucherService.getVoucherById(createVoucherCodeDTO.getVoucherId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        System.out.println("Authenticated user: " + email);

        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()||voucher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            int userPoints = user.get().getPoints();
           int voucherPoints = voucher.get().getPoints();
           int updatedUserPoints = userPoints - voucherPoints;

           if(updatedUserPoints<0) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to redeem: insufficient points.".getBytes());
           }
               else{
                   User user1 = user.get();
                   user1.setPoints(updatedUserPoints);
                   userRepository.save(user1);
                   // Generate PDF in memory
                   ByteArrayOutputStream out = new ByteArrayOutputStream();
                   Document document = new Document();
                   PdfWriter.getInstance(document, out);
                   document.open();

                   document.add(new Paragraph("Voucher Name"));
                   document.add(new Paragraph(voucher.get().getTitle()));
                   document.add(new Paragraph("Voucher Points"));
                    document.add(new Paragraph(String.valueOf(voucher.get().getPoints())));
                   document.add(new Paragraph("Redeem time"));
                    document.add(new Paragraph( new java.util.Date().toString()));
                   document.add(new Paragraph("Voucher Code"));
                   document.add(new Paragraph(code)); // Write the code

                   document.close();

                   // Return as PDF download
                   return ResponseEntity.ok()
                           .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=voucher.pdf")
                           .contentType(MediaType.APPLICATION_PDF)
                           .body(out.toByteArray());

           }
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyVoucher(@RequestBody VerifyCodeDTO verifyCodeDTO) {

        if (voucherCodeService.verifyVoucher(verifyCodeDTO)) {
            return ResponseEntity.ok("Voucher is valid!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or mismatched voucher code.");
        }
    }

    @PostMapping("/redeem/{code}")
    public ResponseEntity<String> redeemVoucher(@PathVariable String code) {
        boolean redeemed = voucherCodeService.redeemVoucher(code);
        if (redeemed) {
            return ResponseEntity.ok("Voucher redeemed successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or already used voucher.");
        }
    }

}

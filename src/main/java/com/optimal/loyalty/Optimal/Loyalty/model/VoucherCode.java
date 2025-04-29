package com.optimal.loyalty.Optimal.Loyalty.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"VoucherCode\"")
public class VoucherCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name = "\"isUsed\"")
    private boolean isUsed = false;
    @Column( name = "\"voucherId\"")
    private int voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"voucherId\"",referencedColumnName = "id", insertable = false, updatable = false)
    private Voucher voucher; // Link to the main Voucher

    // Constructors, getters, setters


    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}

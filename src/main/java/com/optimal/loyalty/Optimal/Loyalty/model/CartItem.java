package com.optimal.loyalty.Optimal.Loyalty.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Id\"")
    private int Id;

    @Column(name = "\"Voucher_id\"")
    private int voucherId;

    @Column(name = "\"User_Id\"")
    private int userId;

    @Column(name = "\"Quantity\"")
    private int quantity;

    // Define the relationship with Voucher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"Voucher_id\"", referencedColumnName = "id", insertable = false, updatable = false)
    private Voucher voucher;

    // Getters and setters for all fields
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
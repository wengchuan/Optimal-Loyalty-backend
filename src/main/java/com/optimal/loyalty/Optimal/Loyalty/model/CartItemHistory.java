package com.optimal.loyalty.Optimal.Loyalty.model;

import jakarta.persistence.*;

@Entity
public class CartItemHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Id\"")
    private int Id;

    @Column(name = "\"voucher_id\"")
    private int voucherId;

    @Column(name = "\"user_Id\"")
    private int userId;

    @Column(name = "\"quantity\"")
    private int quantity;

    private String completed_date;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public String getCompleted_date() {
        return completed_date;
    }

    public void setCompleted_date(String completed_date) {
        this.completed_date = completed_date;
    }
}

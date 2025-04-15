package com.optimal.loyalty.Optimal.Loyalty.dto;

public class AddToCartDTO {
    private int voucherId;
    private int quantity;

    public AddToCartDTO(int voucherId, int quantity) {
        this.voucherId = voucherId;
        this.quantity = quantity;
    }


    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

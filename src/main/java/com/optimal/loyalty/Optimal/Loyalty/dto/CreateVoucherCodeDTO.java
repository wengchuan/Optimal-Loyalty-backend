package com.optimal.loyalty.Optimal.Loyalty.dto;

public class CreateVoucherCodeDTO {
    int userId;

    public CreateVoucherCodeDTO(int voucherId) {
        this.userId = voucherId;
    }

    public int getVoucherId() {
        return userId;
    }

    public void setVoucherId(int userId) {
        this.userId = userId;
    }
}

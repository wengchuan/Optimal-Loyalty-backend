package com.optimal.loyalty.Optimal.Loyalty.dto;

public class VerifyCodeDTO {
    int voucherId;
    String code;

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

package com.optimal.loyalty.Optimal.Loyalty.dto;

public class VoucherCategoryDTO {
    private int categoryId;

    public VoucherCategoryDTO(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}

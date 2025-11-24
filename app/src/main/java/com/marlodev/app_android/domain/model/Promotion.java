package com.marlodev.app_android.domain.model;

import java.util.List;

public class Promotion {
    private int id;
    private String description;
    private String type;
    private String startDate;
    private String endDate;
    private int discountPercent;
    private String code;
    private int maxRedemptions;
    private List<Integer> productIds;

    public Promotion() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMaxRedemptions() {
        return maxRedemptions;
    }

    public void setMaxRedemptions(int maxRedemptions) {
        this.maxRedemptions = maxRedemptions;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds;
    }
}





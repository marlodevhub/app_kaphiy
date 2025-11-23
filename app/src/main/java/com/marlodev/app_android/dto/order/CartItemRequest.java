package com.marlodev.app_android.dto.order;

import java.math.BigDecimal;
import java.util.List;

public class CartItemRequest {
    private Long productId;
    private Long variantId;
    private List<Long> extrasIds;
    private Integer quantity = 1;
    private BigDecimal unitPrice;

    public CartItemRequest(Long productId, Long variantId, List<Long> extrasIds, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.variantId = variantId;
        this.extrasIds = extrasIds;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}

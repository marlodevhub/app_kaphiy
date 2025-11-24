package com.marlodev.app_android.data.network.model.order;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
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

package com.marlodev.app_android.data.network.model.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {
    private Long productId;
    private Long variantId;
    private List<Long> extrasIds;
    private Integer quantity;
    private BigDecimal unitPrice; // opcional (puede ser null)
}
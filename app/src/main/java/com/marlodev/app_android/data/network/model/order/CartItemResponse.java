package com.marlodev.app_android.data.network.model.order;

import com.marlodev.app_android.data.network.model.ProductVariant.ProductVariantResponse;
import com.marlodev.app_android.data.network.model.extra.ExtraResponse;
import com.marlodev.app_android.data.network.model.product.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private ProductVariantResponse variant;
    private List<ExtraResponse> extras;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
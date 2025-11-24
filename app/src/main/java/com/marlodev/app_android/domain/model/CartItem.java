package com.marlodev.app_android.domain.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long id;

    private Product product;
    private ProductVariant variant;

    private List<Extra> extras;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

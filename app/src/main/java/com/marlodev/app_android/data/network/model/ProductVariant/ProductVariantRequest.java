package com.marlodev.app_android.data.network.model.ProductVariant;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequest {
    private String name;
    private BigDecimal price;
    private int stock;
    private Long productId; // Solo enviamos el id del producto
}

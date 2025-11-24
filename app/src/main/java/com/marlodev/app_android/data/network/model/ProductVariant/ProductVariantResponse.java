package com.marlodev.app_android.data.network.model.ProductVariant;

import lombok.*;

        import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private Long productId; // Solo el id del producto
}
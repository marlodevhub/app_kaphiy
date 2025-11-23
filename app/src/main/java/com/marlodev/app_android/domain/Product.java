package com.marlodev.app_android.domain;

import com.marlodev.app_android.dto.product.ProductWebSocketEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private Integer discountPercent;
    private Boolean isNew = false;
    private Double rating = 0.0;
    private Integer reviewsCount = 0;
    private Integer categoryId;
    private Integer storeId;

    private List<ProductVariant> variants = new ArrayList<>();
    private List<Extra> extras = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private List<String> imagePublicIds = new ArrayList<>();

    // Flag para la UI de carga de esqueletos
    @Builder.Default
    private boolean isSkeleton = false;

    // --------------------------
    // 🔹 Conversión desde evento WebSocket
    // --------------------------
    public static Product fromWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null) return null;

        Product product = new Product();
        product.setId(event.getId());
        product.setName(event.getName());
        product.setIsNew(event.getIsNew() != null && event.getIsNew());
        product.setPrice(event.getPrice() != null ? event.getPrice() : BigDecimal.ZERO);

        if (event.getImageUrl() != null && !event.getImageUrl().trim().isEmpty()) {
            product.setImageUrls(List.of(event.getImageUrl()));
        } else {
            product.setImageUrls(new ArrayList<>());
        }

        return product;
    }
}

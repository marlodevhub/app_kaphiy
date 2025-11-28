package com.marlodev.app_android.domain.model;


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

    @Builder.Default
    private boolean isSkeleton = false;
}

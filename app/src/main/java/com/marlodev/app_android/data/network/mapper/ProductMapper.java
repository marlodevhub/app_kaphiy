package com.marlodev.app_android.data.network.mapper;

import com.marlodev.app_android.data.network.model.product.ProductRequest;
import com.marlodev.app_android.data.network.model.product.ProductResponse;
import com.marlodev.app_android.domain.model.Product;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    // 🔹 De Response (API) a Product
    public static Product fromResponse(ProductResponse dto) {
        if (dto == null) return null;

        Product p = new Product();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setOldPrice(dto.getOldPrice());
        p.setDiscountPercent(dto.getDiscountPercent());
        p.setIsNew(dto.getIsNew());
        p.setRating(dto.getRating());
        p.setReviewsCount(dto.getReviewsCount());
        p.setStoreId(dto.getStoreId());
        p.setCategoryId(dto.getCategoryId());
        p.setImageUrls(dto.getImageUrls());
        p.setImagePublicIds(dto.getImagePublicIds());

        p.setVariants(ProductVariantMapper.fromResponseList(dto.getVariants()));
        p.setExtras(ExtraMapper.fromResponseList(dto.getExtras()));
        p.setTags(TagMapper.fromResponseList(dto.getTags()));

        return p;
    }

    public static List<Product> fromResponseList(List<ProductResponse> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) return Collections.emptyList();
        return dtoList.stream().map(ProductMapper::fromResponse).collect(Collectors.toList());
    }



    // 🔹 De Product a Request (para enviar a API)
    public static ProductRequest toRequest(Product p) {
        if (p == null) return null;

        ProductRequest req = new ProductRequest();
        req.setName(p.getName());
        req.setDescription(p.getDescription());
        req.setPrice(p.getPrice());
        req.setOldPrice(p.getOldPrice());
        req.setDiscountPercent(p.getDiscountPercent());
        req.setIsNew(p.getIsNew());
        req.setRating(p.getRating());
        req.setReviewsCount(p.getReviewsCount());
        req.setCategoryId(p.getCategoryId());
        req.setStoreId(p.getStoreId());
        req.setImageUrls(p.getImageUrls());

        req.setVariants(ProductVariantMapper.toRequestList(p.getVariants()));
        req.setExtras(ExtraMapper.toRequestList(p.getExtras()));
        req.setTags(TagMapper.toRequestList(p.getTags()));

        return req;
    }

    public static List<ProductRequest> toRequestList(List<Product> productList) {
        if (productList == null || productList.isEmpty()) return Collections.emptyList();
        return productList.stream().map(ProductMapper::toRequest).collect(Collectors.toList());
    }
}

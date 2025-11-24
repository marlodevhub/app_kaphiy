package com.marlodev.app_android.data.network.mapper;
import com.marlodev.app_android.data.network.model.ProductVariant.ProductVariantRequest;
import com.marlodev.app_android.data.network.model.ProductVariant.ProductVariantResponse;
import com.marlodev.app_android.domain.model.ProductVariant;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantMapper {

    // De Response -> Domain
    public static ProductVariant fromResponse(ProductVariantResponse dto) {
        return ProductVariant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .productId(dto.getProductId())
                .build();
    }

    public static List<ProductVariant> fromResponseList(List<ProductVariantResponse> list) {
        List<ProductVariant> result = new ArrayList<>();
        if (list != null) {
            for (ProductVariantResponse dto : list) {
                result.add(fromResponse(dto));
            }
        }
        return result;
    }

    // De Domain -> Request
    public static ProductVariantRequest toRequest(ProductVariant variant) {
        return ProductVariantRequest.builder()
                .name(variant.getName())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .productId(variant.getProductId())
                .build();
    }

    public static List<ProductVariantRequest> toRequestList(List<ProductVariant> list) {
        List<ProductVariantRequest> result = new ArrayList<>();
        if (list != null) {
            for (ProductVariant v : list) {
                result.add(toRequest(v));
            }
        }
        return result;
    }
}
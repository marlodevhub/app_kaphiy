package com.marlodev.app_android.data.network.mapper;

import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.CartItemResponse;
import com.marlodev.app_android.domain.model.CartItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para CartItem
 * Convierte entre DTOs de red (Request / Response) y modelo de dominio.
 */
public class CartItemMapper {

    // ========================================
    // RESPONSE -> DOMINIO
    // ========================================
    public static CartItem toDomain(CartItemResponse response) {
        if (response == null) return null;

        return CartItem.builder()
                .id(response.getId())
                .product(ProductMapper.fromResponse(response.getProduct()))
                .variant(ProductVariantMapper.fromResponse(response.getVariant()))
                .extras(ExtraMapper.fromResponseList(response.getExtras()))
                .quantity(response.getQuantity())
                .unitPrice(response.getUnitPrice())
                .totalPrice(response.getTotalPrice())
                .build();
    }

    public static List<CartItem> fromResponseList(List<CartItemResponse> responses) {
        if (responses == null || responses.isEmpty()) return Collections.emptyList();
        return responses.stream()
                .map(CartItemMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ========================================
    // DOMINIO -> REQUEST
    // ========================================
    public static CartItemRequest toRequest(CartItem item) {
        if (item == null) return null;

        CartItemRequest req = new CartItemRequest();
        req.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        req.setVariantId(item.getVariant() != null ? item.getVariant().getId() : null);
        req.setExtrasIds(item.getExtras() != null
                ? item.getExtras().stream().map(e -> e.getId()).collect(Collectors.toList())
                : Collections.emptyList());
        req.setQuantity(item.getQuantity());
        req.setUnitPrice(item.getUnitPrice());
        return req;
    }

    public static List<CartItemRequest> toRequestList(List<CartItem> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(CartItemMapper::toRequest)
                .collect(Collectors.toList());
    }
}

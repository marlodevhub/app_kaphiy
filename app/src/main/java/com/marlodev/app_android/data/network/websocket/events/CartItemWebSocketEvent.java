package com.marlodev.app_android.data.network.websocket.events;

import com.google.gson.Gson;
import com.marlodev.app_android.data.network.model.ProductVariant.ProductVariantResponse;
import com.marlodev.app_android.data.network.model.extra.ExtraResponse;
import com.marlodev.app_android.data.network.model.product.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartItemWebSocketEvent {

    private Long id;
    private ProductResponse product;
    private ProductVariantResponse variant;
    private List<ExtraResponse> extras;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String action; // CREATE, UPDATE, DELETE

    public static CartItemWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, CartItemWebSocketEvent.class);
    }
}

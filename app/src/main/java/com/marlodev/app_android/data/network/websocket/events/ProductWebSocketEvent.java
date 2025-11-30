package com.marlodev.app_android.data.network.websocket.events;

import com.google.gson.Gson;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductWebSocketEvent {

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private Double rating = 0.0;
    private Integer reviewsCount = 0;
    private String imageUrl;
    private Boolean isNew;
    private String action; // CREATE, UPDATE, DELETE, IMAGES_UPDATE

    public static ProductWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, ProductWebSocketEvent.class);
    }
}

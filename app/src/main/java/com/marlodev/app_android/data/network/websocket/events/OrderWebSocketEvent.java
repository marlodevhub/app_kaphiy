package com.marlodev.app_android.data.network.websocket.events;

import com.google.gson.Gson;
import com.marlodev.app_android.data.network.model.order.CartItemResponse;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data

public class OrderWebSocketEvent {
    private String action; // CREATE, UPDATE, DELETE
    private OrderResponse order; // El pedido completo

    public static OrderWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, OrderWebSocketEvent.class);
    }
}

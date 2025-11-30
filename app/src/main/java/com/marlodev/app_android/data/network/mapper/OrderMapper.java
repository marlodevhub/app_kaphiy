package com.marlodev.app_android.data.network.mapper;

import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.model.order.OrderTrackingResponse;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.domain.model.OrderTrackingHistory;
import com.marlodev.app_android.domain.model.OrderStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderMapper {

    // RESPONSE -> DOMINIO (Order)
    public static Order fromResponse(OrderResponse dto) {
        if (dto == null) return null;

        List<CartItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (var ci : dto.getItems()) {
                items.add(CartItemMapper.toDomain(ci));
            }
        }

        return Order.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .status(toStatus(dto.getStatus())) // <--- CORREGIDO
                .message(dto.getMessage())
                .totalAmount(dto.getTotalAmount())
                .createdAt(dto.getCreatedAt())
                .confirmedAt(dto.getConfirmedAt())
                .updatedAt(dto.getUpdatedAt())
                .items(items)
                .build();
    }

    public static List<Order> fromResponseList(List<OrderResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();
        List<Order> list = new ArrayList<>();
        for (OrderResponse dto : dtos) {
            list.add(fromResponse(dto));
        }
        return list;
    }

    // DOMINIO -> REQUEST
    public static OrderRequest toRequest(Order domain) {
        if (domain == null) return null;

        List<CartItemRequest> itemsReq = new ArrayList<>();
        if (domain.getItems() != null) {
            for (CartItem ci : domain.getItems()) {
                itemsReq.add(CartItemMapper.toRequest(ci));
            }
        }

        return OrderRequest.builder()
                .userId(domain.getUserId())
                .items(itemsReq)
                .build();
    }

    public static List<OrderRequest> toRequestList(List<Order> domains) {
        if (domains == null || domains.isEmpty()) return Collections.emptyList();
        List<OrderRequest> list = new ArrayList<>();
        for (Order domain : domains) {
            list.add(toRequest(domain));
        }
        return list;
    }




    // RESPONSE -> DOMINIO (OrderTracking)
    public static OrderTracking fromTrackingResponse(OrderTrackingResponse res) {
        if (res == null) return null;

        List<CartItem> items = new ArrayList<>();
        if (res.getItems() != null) {
            for (var ci : res.getItems()) {
                items.add(CartItemMapper.toDomain(ci));
            }
        }

        List<OrderTrackingHistory> history = new ArrayList<>();
        if (res.getHistory() != null) {
            for (var h : res.getHistory()) {
                history.add(OrderTrackingHistoryMapper.fromResponse(h));
            }
        }

        return OrderTracking.builder()
                .orderId(res.getOrderId())
                .currentStatus(toStatus(res.getCurrentStatus())) // <--- CORREGIDO
                .totalAmount(res.getTotalAmount())
                .items(items)
                .history(history)
                .build();
    }

    /**
     * Convierte un String a un Enum OrderStatus de forma segura.
     * @param status El estado como texto (ej. "PENDING").
     * @return El Enum correspondiente o null si no se reconoce.
     */
    private static OrderStatus toStatus(OrderStatus status) {
        // Si ya es null, simplemente retorna null
        return status;
    }

}

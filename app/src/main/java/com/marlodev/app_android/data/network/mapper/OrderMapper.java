package com.marlodev.app_android.data.network.mapper;

import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.model.order.OrderTrackingResponse;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.domain.model.OrderTracking;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper profesional para la entidad Order
 * Convierte entre DTOs de red (Request/Response) y modelos de dominio
 */
public class OrderMapper {

    // ========================================
    // RESPONSE -> DOMINIO (ORDEN)
    // ========================================
    public static Order fromResponse(OrderResponse dto) {
        if (dto == null) return null;

        List<CartItem> items = dto.getItems() == null
                ? Collections.emptyList()
                : dto.getItems().stream()
                .map(CartItemMapper::toDomain)
                .collect(Collectors.toList());

        return Order.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .status(dto.getStatus()) // OrderStatus como enum de dominio
                .message(dto.getMessage())
                .totalAmount(dto.getTotalAmount())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .items(items)
                .build();
    }

    public static List<Order> fromResponseList(List<OrderResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();
        return dtos.stream()
                .map(OrderMapper::fromResponse)
                .collect(Collectors.toList());
    }

    // ========================================
    // DOMINIO -> REQUEST (ORDEN)
    // ========================================
    public static OrderRequest toRequest(Order domain) {
        if (domain == null) return null;

        List<CartItemRequest> itemsReq = domain.getItems() == null
                ? Collections.emptyList()
                : domain.getItems().stream()
                .map(CartItemMapper::toRequest)
                .collect(Collectors.toList());

        return OrderRequest.builder()
                .userId(domain.getUserId())
                .items(itemsReq)
                .build();
    }

    public static List<OrderRequest> toRequestList(List<Order> domains) {
        if (domains == null || domains.isEmpty()) return Collections.emptyList();
        return domains.stream()
                .map(OrderMapper::toRequest)
                .collect(Collectors.toList());
    }

    // ========================================
    // RESPONSE -> DOMINIO (TRACKING)
    // ========================================

    public static OrderTracking toTrackingDomain(OrderTrackingResponse dto) {
        if (dto == null) return null;

        List<CartItem> items = dto.getItems() == null
                ? Collections.emptyList()
                : dto.getItems().stream()
                .map(CartItemMapper::toDomain)
                .collect(Collectors.toList());

        List<OrderTracking.OrderStatusHistory> history = dto.getHistory() == null
                ? Collections.emptyList()
                : dto.getHistory().stream()
                .map(h -> new OrderTracking.OrderStatusHistory(
                        OrderStatus.fromString(h.getStatus()), // <-- aquí convertimos
                        h.getTimestamp(),
                        h.getPerformedBy()
                ))
                .collect(Collectors.toList());

        return OrderTracking.builder()
                .orderId(dto.getOrderId())
                .currentStatus(OrderStatus.fromString(dto.getCurrentStatus())) // <-- aquí también
                .totalAmount(dto.getTotalAmount())
                .items(items)
                .history(history)
                .build();
    }

}

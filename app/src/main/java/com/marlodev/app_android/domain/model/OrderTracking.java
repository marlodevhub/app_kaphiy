package com.marlodev.app_android.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTracking {

    private Long orderId;
    private OrderStatus currentStatus;  // usamos el enum de dominio
    private BigDecimal totalAmount;
    private List<CartItem> items;
    private List<OrderStatusHistory> history;

    // Lista vacía por defecto
    public List<CartItem> getItems() {
        return items == null ? Collections.emptyList() : items;
    }

    public List<OrderStatusHistory> getHistory() {
        return history == null ? Collections.emptyList() : history;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderStatusHistory {
        private OrderStatus status;      // enum de dominio
        private String timestamp;        // lo recibimos como String desde backend
        private String performedBy;
    }
}

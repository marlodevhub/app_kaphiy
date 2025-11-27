package com.marlodev.app_android.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTracking {

    private Long orderId;
    private OrderStatus currentStatus; // <--- CORREGIDO de String a OrderStatus
    private BigDecimal totalAmount;

    private List<CartItem> items;

    private List<OrderTrackingHistory> history;
}

package com.marlodev.app_android.data.network.model.order;

import com.marlodev.app_android.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackingResponse {
    private Long orderId;
    private OrderStatus currentStatus;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
    private List<OrderStatusLog> history;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusLog {
        private String status;
        private String timestamp;   // 🔥 IMPORTANTÍSIMO: ZonedDateTime → String
        private String performedBy;
    }
}
package com.marlodev.app_android.data.network.model.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderTrackingResponse {
    private Long orderId;
    private String currentStatus;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
    private List<OrderStatusLog> history;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public List<OrderStatusLog> getHistory() { return history; }
    public void setHistory(List<OrderStatusLog> history) { this.history = history; }

    // Clase interna para el historial de estados
    public static class OrderStatusLog {
        private String status;
        private String timestamp; // puedes usar String y luego parsear si quieres ZonedDateTime
        private String performedBy;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getPerformedBy() { return performedBy; }
        public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    }
}

package com.marlodev.app_android.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;

    // Usuario que realizó el pedido
    private Integer userId;
    private String username;

    // Estado actual del pedido (ENUM en dominio)
    private OrderStatus status;

    private String message;

    private BigDecimal totalAmount;
    private List<CartItem> items;

    // Fechas
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime confirmedAt;
    private ZonedDateTime preparedAt;
    private ZonedDateTime purchaseDate;
    private ZonedDateTime readyAt;
    private ZonedDateTime deliveredAt;

    // Dirección
    private String deliveryAddress;
    private Double deliveryLat;
    private Double deliveryLng;

    // Store
    private Long storeId;
    private String storeName;

    // Roles asignados
    private Integer baristaId;
    private Integer deliveryId;

    // Métodos útiles
    public int getTotalItemCount() {
        return items == null ? 0 :
                items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal calculateTotalPrice() {
        return items == null ? BigDecimal.ZERO :
                items.stream()
                        .map(CartItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

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
    private String username; // viene del backend

    // Estado actual del pedido
    private String status;

    // Mensaje del backend (opcional)
    private String message;

    // Total del pedido
    private BigDecimal totalAmount;

    // Items de carrito asociados
    private List<CartItem> items;

    // Fechas de flujo
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime confirmedAt;
    private ZonedDateTime preparedAt;
    private ZonedDateTime purchaseDate;
    private ZonedDateTime readyAt;
    private ZonedDateTime deliveredAt;

    // Datos de entrega
    private String deliveryAddress;
    private Double deliveryLat;
    private Double deliveryLng;

    // Store asignada (solo id y name si deseas)
    private Long storeId;
    private String storeName;

    // Roles asignados
    private Integer baristaId;
    private Integer deliveryId;
}

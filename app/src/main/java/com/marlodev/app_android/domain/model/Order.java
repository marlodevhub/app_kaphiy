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

    // Cliente que realiza el pedido
    private Integer userId;
    private String username; // opcional: para UI sin otra llamada

    // Estado del pedido
    private OrderStatus status;
    private String message;

    // Items y totales
    private List<CartItem> items;
    private BigDecimal totalAmount;

    // Fechas importantes
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime confirmedAt;
    private ZonedDateTime preparedAt;
    private ZonedDateTime readyAt;
    private ZonedDateTime deliveredAt;

    // Dirección del pedido (copiada del cliente al momento de crear)
    private String deliveryAddress;
    private Double deliveryLat;
    private Double deliveryLng;

    // Información del store
    private Long storeId;
    private String storeName;

    // Roles asignados (IDs)
    private Integer baristaId;
    private String baristaName; // opcional: para mostrar en UI
    private Integer deliveryId;
    private String deliveryName; // opcional


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

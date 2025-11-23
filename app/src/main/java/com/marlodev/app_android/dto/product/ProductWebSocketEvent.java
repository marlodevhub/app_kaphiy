package com.marlodev.app_android.dto.product;

import com.google.gson.Gson;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductWebSocketEvent {

    // --------------------------
    // 🔹 Campos del evento
    // --------------------------
    private Long id;          // ID del producto afectado
    private String name;      // Nombre del producto
    private BigDecimal price;     // Precio del producto (Gson parsea BigDecimal de backend a Double)
    private String imageUrl;  // URL de la imagen afectada
    private Boolean isNew;    // Mantener el mismo nombre que backend
    private String action;    // CREATE, UPDATE, DELETE, IMAGES_UPDATE

    // --------------------------
    // 🔹 Metodo de utilidad
    // --------------------------
    public static ProductWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, ProductWebSocketEvent.class);
    }
}



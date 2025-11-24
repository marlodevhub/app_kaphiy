package com.marlodev.app_android.data.network.websocket.dto;
import com.google.gson.Gson;

import lombok.Data;

@Data
public class BannerWebSocketEvent {

    // --------------------------
    // 🔹 Campos del evento
    // --------------------------
    private Long id;          // ID del banner afectado
    private String title;     // Título del banner
    private Integer order;    // Orden de aparición
    private String url;       // URL de la imagen
    private String publicId;  // ID público de Cloudinary (opcional)
    private String action;    // CREATE, UPDATE, DELETE

    // --------------------------
    // 🔹 Metodo de utilidad para parsear JSON
    // --------------------------
    public static BannerWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, BannerWebSocketEvent.class);
    }
}

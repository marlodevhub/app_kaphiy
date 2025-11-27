package com.marlodev.app_android.domain.model;

public enum OrderStatus {

    CART,
    PENDIENTE_CONFIRMACION,
    EN_ESPERA,
    EN_PREPARACION,
    LISTO_PARA_ENTREGA,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO;

    public static OrderStatus fromString(String value) {
        if (value == null) return null;

        // Normaliza: mayúsculas, reemplaza espacios, guiones
        String normalized = value
                .trim()
                .toUpperCase()
                .replace("-", "_")
                .replace(" ", "_");

        try {
            return OrderStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null; // FallBack o un DEFAULT si deseas
        }
    }
}


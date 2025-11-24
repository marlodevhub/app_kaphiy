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

    // Conversión segura desde String del backend
    public static OrderStatus fromString(String value) {
        if (value == null) return null;
        try {
            return OrderStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null; // o un fallback
        }
    }
}

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

    // Mensaje legible para mostrar en la app
    public String getDisplayText() {
        return switch(this) {
            case CART -> "Carrito activo";
            case PENDIENTE_CONFIRMACION -> "Esperando confirmación";
            case EN_ESPERA -> "En espera de preparación";
            case EN_PREPARACION -> "Preparando tu pedido";
            case LISTO_PARA_ENTREGA -> "Listo para entrega";
            case EN_CAMINO -> "Tu pedido está en camino";
            case ENTREGADO -> "Pedido entregado";
            case CANCELADO -> "Pedido cancelado";
        };
    }

    // Parse desde string recibido del backend
    public static OrderStatus fromString(String status) {
        try {
            return OrderStatus.valueOf(status);
        } catch (Exception e) {
            return null;
        }
    }
}

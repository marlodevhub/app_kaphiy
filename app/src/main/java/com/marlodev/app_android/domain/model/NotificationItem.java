package com.marlodev.app_android.domain.model;


public class NotificationItem {
    private String mensaje;
    private String tipo;   // "pedido", "stock", "info", "error"
    private String fecha;

    public NotificationItem(String mensaje, String tipo, String fecha) {
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    public String getMensaje() { return mensaje; }
    public String getTipo() { return tipo; }
    public String getFecha() { return fecha; }
}
package com.marlodev.app_android.domain.model;

public class PedidoEntregado {

    private String id;
    private String fecha;
    private String hora;
    private String mes;
    private String cliente;
    private double precio;
    private double propina;

    public PedidoEntregado(String id, String fecha, String hora, String mes,
                           String cliente, double precio, double propina) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.mes = mes;
        this.cliente = cliente;
        this.precio = precio;
        this.propina = propina;
    }

    public String getId() { return id; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getMes() { return mes; }
    public String getCliente() { return cliente; }
    public double getPrecio() { return precio; }
    public double getPropina() { return propina; }
}

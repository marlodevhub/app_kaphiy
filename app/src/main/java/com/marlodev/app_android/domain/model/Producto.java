package com.marlodev.app_android.domain.model;
public class Producto {
    private String nombre;
    private String descripcion;
    private String estado;
    private String cantidad;
    private int imagenResId;

    public Producto(String nombre, String descripcion, String estado, String cantidad, int imagenResId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.cantidad = cantidad;
        this.imagenResId = imagenResId;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getCantidad() { return cantidad; }
    public int getImagenResId() { return imagenResId; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCantidad(String cantidad) { this.cantidad = cantidad; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }




}
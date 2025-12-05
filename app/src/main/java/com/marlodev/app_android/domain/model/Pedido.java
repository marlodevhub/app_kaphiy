package com.marlodev.app_android.domain.model;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * Representa un pedido asignado o disponible para el delivery.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido implements Serializable {

    private int idPedido;
    private String cliente;
    private String direccion;
    private String distancia;
    private String fechaEntrega;
    private double total;
    private double propina;
    private boolean aceptado;
    private boolean entregado;
    private String nombreTienda;

    @Builder.Default
    private boolean isSkeleton = false;
}

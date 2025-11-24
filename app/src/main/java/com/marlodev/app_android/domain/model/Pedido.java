package com.marlodev.app_android.domain.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * Representa un pedido asignado o disponible para el delivery.
 * Usa Lombok para un código más limpio y `equals` y `hashCode` automáticos.
 * Incluye un campo `isSkeleton` para la UI de carga.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido implements Serializable {

    private int id;
    private String cliente;
    private String direccion;
    private String distancia;

    // Flag para indicar si este item es un placeholder de esqueleto.
    @Builder.Default
    private boolean isSkeleton = false;
}

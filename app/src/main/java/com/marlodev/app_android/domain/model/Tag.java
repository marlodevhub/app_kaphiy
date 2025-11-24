package com.marlodev.app_android.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Modelo de dominio para Tag.
 * Incluye una bandera `isSkeleton` para la UI de carga.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    private Integer id;
    private String name;
    private LocalDateTime createdAt;

    // Flag para indicar si este item es un placeholder de esqueleto.
    @Builder.Default
    private boolean isSkeleton = false;
}

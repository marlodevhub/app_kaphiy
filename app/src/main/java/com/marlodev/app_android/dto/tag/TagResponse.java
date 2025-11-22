package com.marlodev.app_android.dto.tag;

import lombok.Data;

/**
 * Data Transfer Object (DTO) para la respuesta de la API de un Tag.
 * Este objeto mapea directamente la estructura del JSON de la red.
 */
@Data
public class TagResponse {
    private Integer id;
    private String name;
    // El campo createdAt se omite si no es necesario para la conversión inicial,
    // o se puede incluir si se va a mapear.
    // private String createdAt; // Ejemplo si la API devuelve una fecha como String
}

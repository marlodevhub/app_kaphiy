package com.marlodev.app_android.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la petición a la API de un Tag.
 * Generalmente se usa para asociar una entidad existente y solo contiene el ID.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {
    private Integer id;
}

package com.marlodev.app_android.dto.tag;

import com.marlodev.app_android.domain.Tag;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper profesional para la entidad Tag.
 * Se encarga de la conversión bidireccional entre los objetos de la capa de Datos (DTOs) y
 * los objetos de la capa de Dominio (el modelo de la aplicación).
 * <p>
 * - DTO de Entrada: {@link TagResponse}
 * - Modelo de Dominio: {@link Tag}
 * - DTO de Salida: {@link TagRequest}
 */

public class TagMapper {

    // --------------------------------------------------------------------
    // --- ENTRADA: Conversión desde DTO de Respuesta -> Modelo de Dominio ---
    // --------------------------------------------------------------------

    /**
     * Convierte un único DTO {@link TagResponse} (de la red) en un modelo de dominio {@link Tag}.
     * Los campos que no vienen en la respuesta (como 'icon' o 'isSelected') se inicializan a valores por defecto.
     * @param dto El objeto de transferencia de datos recibido de la API.
     * @return Un objeto {@link Tag} limpio, o null si la entrada es nula.
     */

    public static Tag fromResponse(TagResponse dto) {

        if (dto == null) {
            return null;
        }
        // Los campos 'icon' y 'isSelected' son estados de la UI, no vienen del DTO de respuesta,
        // por lo que se inicializan a un estado seguro por defecto.
        return new Tag(
                dto.getId(),
                dto.getName(),
                null,    // 'icon' se puede establecer en la capa de UI o ViewModel si es necesario.
                false    // 'isSelected' es un estado puramente de la UI.
        );
    }

    /**
     * Convierte una lista de DTOs {@link TagResponse} en una lista de modelos de dominio {@link Tag}.
     * @param dtoList La lista de DTOs recibida de la API.
     * @return Una lista de {@link Tag}, o una lista vacía si la entrada es nula o está vacía.
     */

    public static List<Tag> fromResponseList(List<TagResponse> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .map(TagMapper::fromResponse)
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // --- SALIDA: Conversión desde Modelo de Dominio -> DTO de Petición  ---
    // --------------------------------------------------------------------

    /**
     * Convierte un objeto de dominio {@link Tag} en un {@link TagRequest} DTO.
     * Cuando se convierte un Tag para asociarlo a otra entidad (ej. un Producto),
     * la API generalmente solo necesita el ID del tag.
     * @param tag El objeto de dominio a convertir.
     * @return Un DTO {@link TagRequest} que contiene solo el ID, o null si el tag o su ID son nulos.
     */
    public static TagRequest toRequest(Tag tag) {
        if (tag == null || tag.getId() == null) {
            return null;
        }
        // Gracias a nuestro TagRequest flexible, esto crea una petición que solo contiene el ID.
        return new TagRequest(tag.getId());
    }

    /**
     * Convierte una lista de modelos de dominio {@link Tag} en una lista de DTOs {@link TagRequest}.
     * @param tags La lista de objetos de dominio.
     * @return Una lista de {@link TagRequest}, o una lista vacía si la entrada es nula o está vacía.
     */
    public static List<TagRequest> toRequestList(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        return tags.stream()
                .map(TagMapper::toRequest)
                .collect(Collectors.toList());
    }

}

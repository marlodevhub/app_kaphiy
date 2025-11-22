package com.marlodev.app_android.dto.tag;

import com.marlodev.app_android.domain.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper bidireccional para convertir entre TagResponse (DTO), Tag (Dominio) y TagRequest (DTO).
 * Centraliza toda la lógica de transformación, desacoplando la capa de red de la de dominio.
 */
public class TagMapper {

    // ------------------------------------------
    // DE LA RESPUESTA DE LA API -> AL DOMINIO
    // ------------------------------------------

    public static Tag fromResponse(TagResponse dto) {
        if (dto == null) {
            return null;
        }
        return new Tag(
                dto.getId(),
                dto.getName(),
                null,
                false
        );
    }

    public static List<Tag> fromResponseList(List<TagResponse> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }
        return dtoList.stream()
                .map(TagMapper::fromResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------
    // DEL DOMINIO -> A LA PETICIÓN PARA LA API
    // ------------------------------------------

    /**
     * Convierte un objeto de dominio Tag a un TagRequest DTO.
     * El DTO solo contendrá el ID, que es lo que la API necesita.
     *
     * @param tag El objeto de dominio.
     * @return Un objeto TagRequest listo para la petición.
     */
    public static TagRequest toRequest(Tag tag) {
        if (tag == null || tag.getId() == null) {
            return null;
        }
        return new TagRequest(tag.getId());
    }

    /**
     * Convierte una lista de objetos de dominio Tag a una lista de TagRequest DTOs.
     * Esto resuelve el error de tipo en ProductMapper.
     *
     * @param tags La lista de objetos de dominio.
     * @return Una lista de TagRequest, lista para ser enviada en un JSON.
     */
    public static List<TagRequest> toRequestList(List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
                .map(TagMapper::toRequest)
                .collect(Collectors.toList());
    }
}

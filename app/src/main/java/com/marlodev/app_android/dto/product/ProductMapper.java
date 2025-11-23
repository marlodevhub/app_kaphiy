package com.marlodev.app_android.dto.product;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.dto.ProductVariant.ProductVariantMapper;
import com.marlodev.app_android.dto.extra.ExtraMapper;
import com.marlodev.app_android.dto.tag.TagMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper profesional para la entidad Product.
 * Se encarga de la conversión bidireccional entre los objetos de la capa de Datos (DTOs) y
 * los objetos de la capa de Dominio (el modelo de la aplicación).
 * <p>
 * - DTO de Entrada: {@link ProductResponse}
 * - Modelo de Dominio: {@link Product}
 * - DTO de Salida: {@link ProductRequest}
 */
public class ProductMapper {

    // --------------------------------------------------------------------
    // --- ENTRADA: Conversión desde DTO de Respuesta -> Modelo de Dominio ---
    // --------------------------------------------------------------------

    /**
     * Convierte un único DTO {@link ProductResponse} (de la red) en un modelo de dominio {@link Product}.
     * Este método es el "traductor" principal que protege la lógica de la aplicación de la estructura de la API.
     * @param dto El objeto de transferencia de datos recibido de la API.
     * @return Un objeto {@link Product} limpio, listo para ser usado en la aplicación.
     */
    public static Product fromResponse(ProductResponse dto) {
        if (dto == null) {
            return null;
        }

        Product p = new Product();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setOldPrice(dto.getOldPrice());
        p.setDiscountPercent(dto.getDiscountPercent());
        p.setIsNew(dto.getIsNew());
        p.setRating(dto.getRating());
        p.setReviewsCount(dto.getReviewsCount());
        p.setStoreId(dto.getStoreId());
        p.setCategoryId(dto.getCategoryId());
        p.setImageUrls(dto.getImageUrls());
        p.setImagePublicIds(dto.getImagePublicIds());

        // Delegación de mapeo para las listas anidadas
        p.setVariants(ProductVariantMapper.fromResponseList(dto.getVariants()));
        p.setExtras(ExtraMapper.fromResponseList(dto.getExtras()));
        p.setTags(TagMapper.fromResponseList(dto.getTags()));

        return p;
    }

    /**
     * Convierte una lista de DTOs {@link ProductResponse} en una lista de modelos de dominio {@link Product}.
     * Utiliza Java Streams para una implementación concisa y moderna.
     * @param dtoList La lista de DTOs recibida de la API.
     * @return Una lista de {@link Product}, o una lista vacía si la entrada es nula.
     */
    public static List<Product> fromResponseList(List<ProductResponse> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .map(ProductMapper::fromResponse)
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // --- SALIDA: Conversión desde Modelo de Dominio -> DTO de Petición  ---
    // --------------------------------------------------------------------

    /**
     * Convierte un modelo de dominio {@link Product} en un DTO {@link ProductRequest} para ser enviado a la API.
     * Prepara el objeto para ser serializado a JSON en una petición de creación o actualización.
     * @param p El objeto de dominio de la aplicación.
     * @return Un DTO {@link ProductRequest} listo para ser enviado en una petición de red.
     */
    public static ProductRequest toRequest(Product p) {
        if (p == null) {
            return null;
        }

        ProductRequest req = new ProductRequest();
        req.setName(p.getName());
        req.setDescription(p.getDescription());
        req.setPrice(p.getPrice());
        req.setOldPrice(p.getOldPrice());
        req.setDiscountPercent(p.getDiscountPercent());
        req.setIsNew(p.getIsNew());
        req.setRating(p.getRating());
        req.setReviewsCount(p.getReviewsCount());
        req.setCategoryId(p.getCategoryId());
        req.setStoreId(p.getStoreId());
        req.setImageUrls(p.getImageUrls());

        // Delegación de mapeo inverso para las listas anidadas
        req.setVariants(ProductVariantMapper.toRequestList(p.getVariants()));
        req.setExtras(ExtraMapper.toRequestList(p.getExtras()));
        req.setTags(TagMapper.toRequestList(p.getTags()));

        return req;
    }

    /**
     * Convierte una lista de modelos de dominio {@link Product} en una lista de DTOs {@link ProductRequest}.
     * @param productList La lista de objetos de dominio.
     * @return Una lista de {@link ProductRequest}, o una lista vacía si la entrada es nula.
     */
    public static List<ProductRequest> toRequestList(List<Product> productList) {
        if (productList == null || productList.isEmpty()) {
            return Collections.emptyList();
        }
        return productList.stream()
                .map(ProductMapper::toRequest)
                .collect(Collectors.toList());
    }
}

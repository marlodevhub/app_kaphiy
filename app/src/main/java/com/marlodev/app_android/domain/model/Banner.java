package com.marlodev.app_android.domain.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de banner profesional para ViewPager2.
 * Incluye una bandera `isSkeleton` para la UI de carga.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    private Long id;
    private String title;
    private Integer order;
    private String url;
    private String publicId;

    // Flag para indicar si este item es un placeholder de esqueleto.
    @Builder.Default
    private boolean isSkeleton = false;

    // --------------------------
    // 🔹 Utilidades profesionales
    // --------------------------

    public boolean hasImage() {
        return url != null && !url.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Banner banner)) return false;
        return Objects.equals(id, banner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
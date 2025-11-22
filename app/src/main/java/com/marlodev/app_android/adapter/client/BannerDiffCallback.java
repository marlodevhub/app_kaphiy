package com.marlodev.app_android.adapter.client;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.domain.Banner;

import java.util.Objects;

/**
 * DiffUtil.ItemCallback para el modelo Banner.
 * Permite a ListAdapter calcular de manera eficiente las actualizaciones de la lista en un hilo de fondo,
 * mejorando el rendimiento y habilitando animaciones automáticas.
 */
public class BannerDiffCallback extends DiffUtil.ItemCallback<Banner> {

    /**
     * Determina si dos objetos representan el mismo ítem. 
     * Si la API garantiza IDs únicos, este es el mejor campo para la comparación.
     */
    @Override
    public boolean areItemsTheSame(@NonNull Banner oldItem, @NonNull Banner newItem) {
        // Los esqueletos no tienen ID, así que los tratamos como elementos únicos por su posición.
        if (oldItem.isSkeleton() && newItem.isSkeleton()) {
            return true;
        }
        // Si uno es esqueleto y el otro no, no son el mismo ítem.
        if (oldItem.isSkeleton() || newItem.isSkeleton()) {
            return false;
        }
        // Para banners reales, el ID es la única fuente de verdad.
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    /**
     * Determina si el contenido de dos ítems es el mismo.
     * Se llama solo si areItemsTheSame() devuelve true.
     * Compara los datos visuales para ver si es necesario redibujar el ViewHolder.
     */
    @Override
    public boolean areContentsTheSame(@NonNull Banner oldItem, @NonNull Banner newItem) {
        // Objects.equals maneja de forma segura los posibles nulos en los campos.
        return Objects.equals(oldItem, newItem);
    }
}

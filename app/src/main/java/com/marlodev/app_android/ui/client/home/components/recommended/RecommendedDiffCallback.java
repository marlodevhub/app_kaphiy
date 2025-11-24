package com.marlodev.app_android.ui.client.home.components.recommended;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.domain.model.Product;

import java.util.Objects;

/**
 * DiffUtil.ItemCallback profesional para el modelo Product.
 * Es crucial para que ListAdapter funcione de manera eficiente.
 */
public class RecommendedDiffCallback extends DiffUtil.ItemCallback<Product> {

    @Override
    public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
        // Si los items son esqueletos, los consideramos iguales si ambos lo son.
        if (oldItem.isSkeleton() && newItem.isSkeleton()) {
            return true;
        }
        // Si solo uno es esqueleto, no son iguales.
        if (oldItem.isSkeleton() || newItem.isSkeleton()) {
            return false;
        }
        // Si son items reales, comparamos por su ID único.
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
        // Si los items son esqueletos, su contenido es "igual" (no cambia).
        if (oldItem.isSkeleton() && newItem.isSkeleton()) {
            return true;
        }
        // Comparamos el contenido real del objeto. Lombok genera el método equals() por nosotros.
        return oldItem.equals(newItem);
    }
}

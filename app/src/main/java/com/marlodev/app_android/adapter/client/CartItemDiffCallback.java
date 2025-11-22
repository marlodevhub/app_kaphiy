package com.marlodev.app_android.adapter.client;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.model.order.CartItem;

import java.util.Objects;

/**
 * DiffUtil.ItemCallback para el modelo CartItem.
 * Permite a ListAdapter calcular de manera eficiente las actualizaciones en la lista del carrito.
 * Es la clave para habilitar animaciones suaves y profesionales al añadir, eliminar o actualizar items.
 */
public class CartItemDiffCallback extends DiffUtil.ItemCallback<CartItem> {

    /**
     * Determina si dos objetos representan el mismo ítem.
     * El ID del CartItem es la única fuente de verdad.
     */
    @Override
    public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    /**
     * Determina si el contenido de dos ítems es el mismo.
     * Se llama solo si areItemsTheSame() devuelve true.
     * Aquí comparamos la cantidad y los detalles del producto para ver si es necesario redibujar.
     */
    @Override
    public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
        // Compara tanto la cantidad como la igualdad del objeto producto.
        // Esto asegura que si el precio o nombre del producto cambiara, la UI también se actualizaría.
        return oldItem.getQuantity() == newItem.getQuantity() &&
               Objects.equals(oldItem.getProduct(), newItem.getProduct());
    }
}

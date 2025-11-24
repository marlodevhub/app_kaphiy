package com.marlodev.app_android.ui.client.cart;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.data.network.model.order.CartItem;

import java.util.Objects;

/**
 * DiffUtil.ItemCallback profesional para el modelo CartItem.
 * Permite a ListAdapter calcular de manera eficiente las actualizaciones en la lista del carrito.
 * Es la clave para habilitar animaciones suaves y profesionales al añadir, eliminar o actualizar items.
 */
public class CartItemDiffCallback extends DiffUtil.ItemCallback<CartItem> {

    /**
     * Determina si dos objetos representan el mismo ítem en la lista.
     * Esta comparación DEBE basarse en un identificador único y estable.
     * En nuestro caso, es el ID del CartItem (la línea del carrito).
     */
    @Override
    public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
        // Si los IDs son iguales, DiffUtil sabe que es el mismo item y no creará una nueva vista.
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    /**
     * Determina si el contenido de dos ítems (que ya sabemos que son el mismo por su ID) ha cambiado.
     * Si esto devuelve false, el RecyclerView redibujará la vista para mostrar los nuevos datos (ej. la nueva cantidad).
     */
    @Override
    public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
        // Comparamos la cantidad. Si es diferente, el contenido cambió.
        boolean isQuantitySame = oldItem.getQuantity() == newItem.getQuantity();

        // Comparamos los productos internos por su ID para más robustez.
        boolean isProductSame = Objects.equals(oldItem.getProduct(), newItem.getProduct());

        // La vista solo necesita ser redibujada si la cantidad o los detalles del producto cambian.
        return isQuantitySame && isProductSame;
    }
}

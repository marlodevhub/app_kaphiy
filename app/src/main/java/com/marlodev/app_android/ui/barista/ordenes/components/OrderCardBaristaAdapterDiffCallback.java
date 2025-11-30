package com.marlodev.app_android.ui.barista.ordenes.components;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.domain.model.Order;

import java.util.Objects;

/**
 * DiffUtil.ItemCallback profesional para Order en el Adapter del barista.
 * Compara correctamente items y su contenido para actualizar solo los cambios necesarios.
 */
public class OrderCardBaristaAdapterDiffCallback extends DiffUtil.ItemCallback<Order> {

    @Override
    public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
        // Mismo ID significa que es la misma orden
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
        // Compara todos los campos importantes
        return Objects.equals(oldItem.getId(), newItem.getId()) &&
                Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
//                Objects.equals(oldItem.getClientName(), newItem.getClientName()) &&
                Objects.equals(oldItem.getCreatedAt(), newItem.getCreatedAt()) ;
//                &&
//                Objects.equals(oldItem.getLocation(), newItem.getLocation()) &&
//                Objects.equals(oldItem.getProducts(), newItem.getProducts()) &&
//                Objects.equals(oldItem.getTotalPrice(), newItem.getTotalPrice());
    }

    @Override
    public Object getChangePayload(@NonNull Order oldItem, @NonNull Order newItem) {
        // Puedes usar esto para animaciones parciales si solo cambia status, por ejemplo
        if (!Objects.equals(oldItem.getStatus(), newItem.getStatus())) {
            return newItem.getStatus();
        }
        return super.getChangePayload(oldItem, newItem);
    }
}

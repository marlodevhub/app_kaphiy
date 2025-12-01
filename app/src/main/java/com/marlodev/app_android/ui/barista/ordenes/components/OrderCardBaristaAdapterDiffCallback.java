package com.marlodev.app_android.ui.barista.ordenes.components;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.domain.model.Order;

import java.util.List;
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
        if (!Objects.equals(oldItem.getId(), newItem.getId())) return false;
        if (!Objects.equals(oldItem.getStatus(), newItem.getStatus())) return false;
        if (!Objects.equals(oldItem.getCreatedAt(), newItem.getCreatedAt())) return false;

        // Compara items / productos
        if (oldItem.getItems() == null && newItem.getItems() == null) return true;
        if (oldItem.getItems() == null || newItem.getItems() == null) return false;
        if (oldItem.getItems().size() != newItem.getItems().size()) return false;

        for (int i = 0; i < oldItem.getItems().size(); i++) {
            List<String> oldImages = oldItem.getItems().get(i).getProduct().getImageUrls();
            List<String> newImages = newItem.getItems().get(i).getProduct().getImageUrls();
            if (!Objects.equals(oldImages, newImages)) return false;
        }

        return true;
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

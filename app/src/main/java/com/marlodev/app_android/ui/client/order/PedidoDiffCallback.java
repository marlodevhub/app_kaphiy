package com.marlodev.app_android.ui.client.order;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.domain.model.Pedido;

public class PedidoDiffCallback extends DiffUtil.ItemCallback<Pedido> {

    @Override
    public boolean areItemsTheSame(@NonNull Pedido oldItem, @NonNull Pedido newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Pedido oldItem, @NonNull Pedido newItem) {
        return oldItem.equals(newItem);
    }
}

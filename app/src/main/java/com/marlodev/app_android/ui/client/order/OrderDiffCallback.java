package com.marlodev.app_android.ui.client.order;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.data.network.model.order.OrderResponse;

import java.util.Objects;

public class OrderDiffCallback extends DiffUtil.ItemCallback<OrderResponse> {

    @Override
    public boolean areItemsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
        return oldItem.equals(newItem);
    }
}

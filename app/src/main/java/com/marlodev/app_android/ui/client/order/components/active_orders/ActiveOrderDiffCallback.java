package com.marlodev.app_android.ui.client.order.components.active_orders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.domain.model.Order;

import java.util.Objects;

public class ActiveOrderDiffCallback extends DiffUtil.ItemCallback<Order> {

    @Override
    public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
        return  Objects.equals(oldItem.getId(), newItem.getId());

    }

    @Override
    public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
        boolean isDate = oldItem.getCreatedAt() == newItem.getCreatedAt();
        return isDate;
    }

}
package com.marlodev.app_android.ui.client.order.components.active_orders;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.databinding.ItemPedidoHistorialBinding;
import com.marlodev.app_android.data.network.model.order.CartItemResponse;
import com.marlodev.app_android.data.network.model.order.OrderResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ActiveOrderAdapter extends ListAdapter<OrderResponse, ActiveOrderAdapter.ViewHolder> {

    public ActiveOrderAdapter() {
        super(new ActiveOrderDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPedidoHistorialBinding binding = ItemPedidoHistorialBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = getItem(position);
        holder.bind(order);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPedidoHistorialBinding binding;

        public ViewHolder(ItemPedidoHistorialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderResponse order) {
            binding.tvOrderStatus.setText(order.getStatus());
            binding.tvOrderMessage.setText(order.getMessage());

            if (order.getCreatedAt() != null) {
                try {
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM", new Locale("es", "PE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                binding.textDate.setText("");
            }

            List<CartItemResponse> items = order.getItems();
            if (items != null && !items.isEmpty()) {
                CartItemResponse lastItem = items.get(items.size() - 1);
                List<String> images = lastItem.getProduct().getImageUrls();
                if (images != null && !images.isEmpty()) {
                    String lastImageUrl = images.get(images.size() - 1);
                    Glide.with(itemView.getContext())
                            .load(lastImageUrl)
                            .into(binding.itemImage);
                } else {
                    binding.itemImage.setImageResource(android.R.color.transparent);
                }
            } else {
                binding.itemImage.setImageResource(android.R.color.transparent);
            }
        }
    }
}

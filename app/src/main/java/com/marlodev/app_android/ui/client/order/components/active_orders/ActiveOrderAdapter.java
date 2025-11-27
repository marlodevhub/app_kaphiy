package com.marlodev.app_android.ui.client.order.components.active_orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActiveOrderAdapter extends ListAdapter<Order, ActiveOrderAdapter.ViewHolder> {

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ActiveOrderAdapter() {
        super(new ActiveOrderDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = getItem(position);

        if (order == null) {
            holder.bindError();
            return;
        }

        holder.bind(order, formatter);
    }

    // -----------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtOrderDate;
        private final TextView txtOrderStatus;
        private final ImageView imgOrderClient;
        private final TextView txtOrderMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderDate = itemView.findViewById(R.id.txt_order_date);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
            imgOrderClient = itemView.findViewById(R.id.img_order_client);
            txtOrderMessage = itemView.findViewById(R.id.txt_order_message);
        }

        public void bind(Order order, DateTimeFormatter formatter) {
            // Fecha
            if (order.getCreatedAt() != null) {
                txtOrderDate.setText(order.getCreatedAt().format(formatter));
            } else {
                txtOrderDate.setText("Sin fecha");
            }

            // Estado
            if (order.getStatus() != null) {
                txtOrderStatus.setText(order.getStatus().name());
            } else {
                txtOrderStatus.setText("Sin estado");
            }

            // Mensaje (si existe) y visibilidad
            if (order.getMessage() != null && !order.getMessage().isEmpty()) {
                txtOrderMessage.setText(order.getMessage());
                txtOrderMessage.setVisibility(View.VISIBLE);
            } else {
                txtOrderMessage.setVisibility(View.GONE);
            }

            // --- Cargar la imagen del último producto ---
            List<String> imageUrls = null;
            List<CartItem> items = order.getItems();
            if (items != null && !items.isEmpty()) {
                // 1. Obtener el último item del carrito
                CartItem lastItem = items.get(items.size() - 1);
                if (lastItem != null && lastItem.getProduct() != null) {
                    // 2. Obtener las URLs de la imagen de ese producto
                    imageUrls = lastItem.getProduct().getImageUrls();
                }
            }
            // 3. Cargar la imagen (el metodo se encarga si es nulo)
            loadOrderImage(imageUrls);
        }

        /**
         * Carga la primera imagen de la lista de URLs en el ImageView.
         */
        private void loadOrderImage(List<String> imageUrls) {
            if (imageUrls != null && !imageUrls.isEmpty() && imageUrls.get(0) != null) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(imgOrderClient);
            } else {
                imgOrderClient.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        public void bindError() {
            txtOrderDate.setText("Fecha no disponible");
            txtOrderStatus.setText("Estado no disponible");
            imgOrderClient.setImageResource(R.drawable.ic_image_placeholder);
            txtOrderMessage.setVisibility(View.GONE);
        }
    }
}

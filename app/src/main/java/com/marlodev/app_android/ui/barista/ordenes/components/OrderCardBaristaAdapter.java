package com.marlodev.app_android.ui.barista.ordenes.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.List;

public class OrderCardBaristaAdapter extends ListAdapter<Order, OrderCardBaristaAdapter.OrderViewHolder> {

    private OnItemClickListener listener;

    public OrderCardBaristaAdapter() {
        super(new OrderCardBaristaAdapterDiffCallback());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barista_orden, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.bind(order);
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtOrderNumber;
        private final TextView txtStatus;
        private final TextView txtTiempoLlegada;
        private final TextView txtDirection;
        private final TextView txtCantidad;
        private final ImageView imgeOrder;
        private final TextView txtTitleOrder;
        private final TextView txtClientName;
        private final Button btnPreparar;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTiempoLlegada = itemView.findViewById(R.id.txtTiempoLlegada);
            txtDirection = itemView.findViewById(R.id.textDirection);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            imgeOrder = itemView.findViewById(R.id.imgeOrder);
            txtTitleOrder = itemView.findViewById(R.id.textTitleOrder);
            txtClientName = itemView.findViewById(R.id.txtClientName);
            btnPreparar = itemView.findViewById(R.id.btnPreparar);

            btnPreparar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPrepareClick(getItem(position));
                }
            });
        }

        public void bind(Order order) {
            txtOrderNumber.setText("#00" + order.getId());
            txtClientName.setText(order.getUsername());

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
            loadProductImage(imageUrls);
        }

        private void loadProductImage(List<String> imageUrls) {
            if (imageUrls != null && !imageUrls.isEmpty() && imageUrls.get(0) != null) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(imgeOrder);
            } else {
                imgeOrder.setImageResource(R.drawable.ic_image_placeholder);
            }
        }
    }

    public interface OnItemClickListener {
        void onPrepareClick(Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}

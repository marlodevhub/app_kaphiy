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
import com.marlodev.app_android.domain.model.OrderStatus;

import java.util.List;

public class OrderCardBaristaAdapter extends ListAdapter<Order, OrderCardBaristaAdapter.OrderViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onStartPreparation(Order order);
        void onOpenOrderDetail(Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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
        holder.bind(getItem(position));
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtOrderNumber, txtClientName;
        private final ImageView imgeOrder;
        private final Button btnPreparar;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
            txtClientName = itemView.findViewById(R.id.txtClientName);
            imgeOrder = itemView.findViewById(R.id.imgeOrder);
            btnPreparar = itemView.findViewById(R.id.btnPreparar);

            btnPreparar.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    Order order = getItem(pos);
                    if (order.getStatus() == OrderStatus.EN_ESPERA) {
                        listener.onStartPreparation(order);
                    } else if (order.getStatus() == OrderStatus.EN_PREPARACION) {
                        listener.onOpenOrderDetail(order);
                    }
                }
            });
        }

        public void bind(Order order) {
            txtOrderNumber.setText("#00" + order.getId());
            txtClientName.setText(order.getUsername());

            OrderStatus status = order.getStatus();
            if (status != null) {
                switch (status) {
                    case EN_ESPERA:
                        btnPreparar.setText("Tomar orden"); btnPreparar.setEnabled(true);
                        break;
                    case EN_PREPARACION:
                        btnPreparar.setText("Preparar"); btnPreparar.setEnabled(true);
                        break;
                    default:
                        btnPreparar.setText("Acción"); btnPreparar.setEnabled(false);
                        break;
                }
            }

            // Imagen
            List<String> imageUrls = null;
            List<CartItem> items = order.getItems();
            if (items != null && !items.isEmpty()) {
                CartItem lastItem = items.get(items.size() - 1);
                if (lastItem != null && lastItem.getProduct() != null)
                    imageUrls = lastItem.getProduct().getImageUrls();
            }
            String url = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
            Glide.with(itemView.getContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(imgeOrder);
        }
    }
}

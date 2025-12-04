package com.marlodev.app_android.ui.barista.mas.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.CartItem;

public class OrderItemAdapter extends ListAdapter<CartItem, OrderItemAdapter.ViewHolder> {

    public OrderItemAdapter() { super(DIFF_CALLBACK); }

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barista_orden_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = getItem(position);

        // Nombre del producto + variante si existe
        String productName = item.getProduct().getName();
        if (item.getVariant() != null && item.getVariant().getName() != null) {
            productName += " | " + item.getVariant().getName();
        }
        holder.tvName.setText(productName);

        // Cantidad
        holder.tvQuantity.setText("x" + item.getQuantity());

        // Precio total (unitario * cantidad)
        if (item.getTotalPrice() != null) {
            holder.tvPrice.setText("$" + item.getTotalPrice().toPlainString());
        } else if (item.getUnitPrice() != null && item.getQuantity() != null) {
            holder.tvPrice.setText("$" + item.getUnitPrice().multiply(
                    new java.math.BigDecimal(item.getQuantity())
            ).toPlainString());
        } else {
            holder.tvPrice.setText("$0.00");
        }

        // Imagen
        String imageUrl = null;
        if (item.getProduct() != null && item.getProduct().getImageUrls() != null
                && !item.getProduct().getImageUrls().isEmpty()) {
            imageUrl = item.getProduct().getImageUrls().get(0); // primera imagen
        }

        if (imageUrl != null) {
            Glide.with(holder.imgProduct.getContext())
                    .load(imageUrl)
//                    .placeholder(R.drawable.ic_placeholder) // imagen por defecto
                    .into(holder.imgProduct);
        } else {
//            holder.imgProduct.setImageResource(R.drawable.ic_placeholder);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice;
        ImageView imgProduct; // <- referencia al ImageView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtTitle);
            tvQuantity = itemView.findViewById(R.id.txtQuantity);
            tvPrice = itemView.findViewById(R.id.txtPrice); // si tienes
            imgProduct = itemView.findViewById(R.id.imgItemOrder); // <- aquí
        }
    }

}

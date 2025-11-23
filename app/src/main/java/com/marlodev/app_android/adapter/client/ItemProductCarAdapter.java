package com.marlodev.app_android.adapter.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.dto.order.CartItem;

import java.util.ArrayList;
import java.util.List;

public class ItemProductCarAdapter extends RecyclerView.Adapter<ItemProductCarAdapter.ViewHolder> {

    private final List<CartItem> items = new ArrayList<>();
    private OnQuantityChangeListener quantityListener;
    private OnDeleteClickListener deleteListener;


    public interface OnQuantityChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
    }

    public interface OnDeleteClickListener {
        void onDelete(CartItem item);
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setItems(List<CartItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        var product = item.getProduct();

        holder.itemName.setText(product.getName());
        holder.currentPrice.setText("S/. " + product.getPrice());

        if (product.getOldPrice() != null) {
            holder.oldPrice.setText("S/. " + product.getOldPrice());
            holder.oldPrice.setVisibility(View.VISIBLE);
        } else {
            holder.oldPrice.setVisibility(View.GONE);
        }

        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        // Load first image
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrls().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.itemImage);
        }

        // Increment quantity
        holder.plusButton.setOnClickListener(v -> {
            int qty = item.getQuantity() + 1;
            item.setQuantity(qty);
            holder.quantityText.setText(String.valueOf(qty));
            if (quantityListener != null) quantityListener.onQuantityChanged(item, qty);
        });

        // Decrement quantity
        holder.minusButton.setOnClickListener(v -> {
            int qty = item.getQuantity();
            if (qty > 1) {
                qty--;
                item.setQuantity(qty);
                holder.quantityText.setText(String.valueOf(qty));
                if (quantityListener != null) quantityListener.onQuantityChanged(item, qty);
            }
        });

        // Delete item
        holder.deleteIcon.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, currentPrice, oldPrice, quantityText;
        ImageButton plusButton, minusButton;
        ImageView deleteIcon; // ← CORRECTO (ImageView)

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            currentPrice = itemView.findViewById(R.id.current_price);
            oldPrice = itemView.findViewById(R.id.old_price);
            quantityText = itemView.findViewById(R.id.quantity_text);

            plusButton = itemView.findViewById(R.id.plus_button);
            minusButton = itemView.findViewById(R.id.minus_button);

            // Asignación correcta
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }
}

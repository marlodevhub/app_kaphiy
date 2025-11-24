package com.marlodev.app_android.ui.client.cart.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.CartItem;

/**
 * Adaptador profesional para la lista de items del carrito.
 * Usa ListAdapter para manejar de forma eficiente las actualizaciones de la lista, 
 * previniendo la duplicación de vistas y permitiendo animaciones suaves.
 */
public class ItemProductCarAdapter extends ListAdapter<CartItem, ItemProductCarAdapter.ViewHolder> {

    private OnQuantityChangeListener quantityListener;
    private OnDeleteClickListener deleteListener;

    public ItemProductCarAdapter() {
        super(new CartItemDiffCallback());
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = getItem(position);
        var product = item.getProduct();

        if (product == null) return; // Guarda de seguridad por si el producto es nulo

        holder.itemName.setText(product.getName());
        holder.currentPrice.setText("S/. " + product.getPrice());

        if (product.getOldPrice() != null) {
            holder.oldPrice.setText("S/. " + product.getOldPrice());
            holder.oldPrice.setVisibility(View.VISIBLE);
        } else {
            holder.oldPrice.setVisibility(View.GONE);
        }

        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrls().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.itemImage);
        }

        holder.plusButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            // No actualizamos la UI directamente. Delegamos al ViewModel.
            if (quantityListener != null) quantityListener.onQuantityChanged(item, newQuantity);
        });

        holder.minusButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                if (quantityListener != null) quantityListener.onQuantityChanged(item, newQuantity);
            }
        });

        holder.deleteIcon.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(item);
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, currentPrice, oldPrice, quantityText;
        ImageButton plusButton, minusButton;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            currentPrice = itemView.findViewById(R.id.current_price);
            oldPrice = itemView.findViewById(R.id.old_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            plusButton = itemView.findViewById(R.id.plus_button);
            minusButton = itemView.findViewById(R.id.minus_button);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }
}

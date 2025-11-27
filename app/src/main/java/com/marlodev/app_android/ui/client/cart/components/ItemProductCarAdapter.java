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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.CartItem;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ItemProductCarAdapter extends ListAdapter<CartItem, ItemProductCarAdapter.ViewHolder> {

    private static final int MAX_QUANTITY = 99;
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));

    private OnQuantityChangeListener quantityListener;
    private OnDeleteClickListener deleteListener;
    private OnItemClickListener itemClickListener;

    public ItemProductCarAdapter() {
        super(new CartItemDiffCallback());
        CURRENCY_FORMAT.setMaximumFractionDigits(2);
        CURRENCY_FORMAT.setMinimumFractionDigits(2);
    }

    // ----------------------------------------------
    // INTERFACES PARA EVENTOS
    // ----------------------------------------------
    public interface OnQuantityChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
    }

    public interface OnDeleteClickListener {
        void onDelete(CartItem item);
    }

    public interface OnItemClickListener {
        void onItemClick(CartItem item);
    }

    // ----------------------------------------------
    // SETTERS DE LISTENERS
    // ----------------------------------------------
    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // ----------------------------------------------
    // CREAR VISTA
    // ----------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_car, parent, false);
        return new ViewHolder(view);
    }

    // ----------------------------------------------
    // BIND DE DATOS
    // ----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = getItem(position);
        if (item == null || item.getProduct() == null) {
            bindErrorState(holder);
            return;
        }

        holder.bind(item, quantityListener, deleteListener, itemClickListener);
    }

    /**
     * Maneja el estado cuando el item o producto es nulo
     */
    private void bindErrorState(@NonNull ViewHolder holder) {
        holder.itemName.setText("Error al cargar producto");
        holder.currentPrice.setText("S/. 0.00");
        holder.oldPrice.setVisibility(View.GONE);
        holder.quantityText.setText("0");
        holder.itemImage.setImageResource(R.drawable.ic_image_placeholder);
        holder.disableInteractions();
    }

    // ----------------------------------------------
    // VISTA HOLDER OPTIMIZADO
    // ----------------------------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final TextView itemName;
        private final TextView currentPrice;
        private final TextView oldPrice;
        private final TextView quantityText;
        private final ImageButton plusButton;
        private final ImageButton minusButton;
        private final ImageView deleteIcon;

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

        /**
         * Vincula los datos del CartItem con la vista
         */
        public void bind(
                @NonNull CartItem item,
                OnQuantityChangeListener quantityListener,
                OnDeleteClickListener deleteListener,
                OnItemClickListener itemClickListener
        ) {
            var product = item.getProduct();
            if (product == null) return;

            // ---- Datos básicos ----
            itemName.setText(product.getName() != null ? product.getName() : "Sin nombre");

            // Formateo de precio con manejo de nulos
            BigDecimal price = product.getPrice();
            currentPrice.setText(formatPrice(price));

            // Precio antiguo (si existe)
            BigDecimal oldPriceValue = product.getOldPrice();
            if (oldPriceValue != null && oldPriceValue.compareTo(BigDecimal.ZERO) > 0) {
                oldPrice.setText(formatPrice(oldPriceValue));
                oldPrice.setVisibility(View.VISIBLE);
            } else {
                oldPrice.setVisibility(View.GONE);
            }

            // Cantidad
            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            quantityText.setText(String.valueOf(quantity));

            // ---- Imagen del producto con manejo de errores ----
            loadProductImage(product.getImageUrls());

            // ---- Click en el item completo ----
            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            });

            // ---- Botón aumentar cantidad ----
            plusButton.setEnabled(quantity < MAX_QUANTITY);
            plusButton.setOnClickListener(v -> {
                if (quantityListener != null && quantity < MAX_QUANTITY) {
                    quantityListener.onQuantityChanged(item, quantity + 1);
                }
            });

            // ---- Botón disminuir cantidad ----
            minusButton.setEnabled(quantity > 1);
            minusButton.setOnClickListener(v -> {
                if (quantityListener != null && quantity > 1) {
                    quantityListener.onQuantityChanged(item, quantity - 1);
                }
            });

            // ---- Eliminar item ----
            deleteIcon.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(item);
                }
            });
        }

        /**
         * Carga la imagen del producto con Glide y manejo de errores
         */
        private void loadProductImage(List<String> imageUrls) {
            if (imageUrls != null && !imageUrls.isEmpty() && imageUrls.get(0) != null) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(itemImage);
            } else {
                itemImage.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        /**
         * Formatea el precio según la configuración regional
         */
        private String formatPrice(BigDecimal price) {
            if (price == null) {
                return "S/. 0.00";
            }
            return CURRENCY_FORMAT.format(price);
        }

        /**
         * Deshabilita las interacciones cuando hay un error
         */
        public void disableInteractions() {
            plusButton.setEnabled(false);
            minusButton.setEnabled(false);
            deleteIcon.setEnabled(false);
            itemView.setOnClickListener(null);
        }
    }
}
package com.marlodev.app_android.ui.client.home.components.recommended;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemPopularSkeletonBinding;
import com.marlodev.app_android.databinding.ItemProductPopularBinding;
import com.marlodev.app_android.domain.model.Product;

import java.util.List;

public class RecommendedAdapter extends ListAdapter<Product, RecyclerView.ViewHolder> {

    private OnProductClickListener listener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SKELETON = 1;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public RecommendedAdapter() {
        super(new RecommendedDiffCallback());
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isSkeleton() ? VIEW_TYPE_SKELETON : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SKELETON) {
            ItemPopularSkeletonBinding skeletonBinding = ItemPopularSkeletonBinding.inflate(inflater, parent, false);
            return new SkeletonViewHolder(skeletonBinding);
        } else {
            ItemProductPopularBinding binding = ItemProductPopularBinding.inflate(inflater, parent, false);
            return new ProductViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            productViewHolder.bind(getItem(position), listener);
        }
    }

    // --- ViewHolders ---

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductPopularBinding binding;

        public ProductViewHolder(@NonNull ItemProductPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product, OnProductClickListener listener) {
            binding.txtTitle.setText(product.getName() != null ? product.getName() : "Sin nombre");

            List<String> images = product.getImageUrls();
            if (images != null && !images.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(images.get(0))
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .centerCrop()
                        .into(binding.pic);
            } else {
                binding.pic.setImageResource(R.drawable.ic_image_placeholder);
            }

            binding.txtNuevo.setVisibility(product.getIsNew() != null && product.getIsNew() ? View.VISIBLE : View.GONE);
            binding.txtPrece.setText("S/." + product.getPrice());
            binding.txtOldPrece.setText("S/." + product.getOldPrice());
            binding.txtRanking.setText(String.valueOf(product.getRating()));
            binding.txtReviews.setText("(" + product.getReviewsCount() + ")");

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        public SkeletonViewHolder(ItemPopularSkeletonBinding binding) {
            super(binding.getRoot());
        }
    }
}

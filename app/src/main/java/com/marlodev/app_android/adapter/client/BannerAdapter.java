package com.marlodev.app_android.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemBannerSkeletonBinding;
import com.marlodev.app_android.databinding.ItemSliderBinding;
import com.marlodev.app_android.domain.Banner;

/**
 * Adaptador profesional para el ViewPager2 de banners, refactorizado para usar ListAdapter.
 * Esta implementación es más eficiente, proporciona animaciones automáticas y simplifica la gestión de la lista.
 */
public class BannerAdapter extends ListAdapter<Banner, RecyclerView.ViewHolder> {

    private final Context context;
    private OnBannerClickListener clickListener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SKELETON = 1;

    public BannerAdapter(Context context, ViewPager2 viewPager2) {
        super(new BannerDiffCallback());
        this.context = context;
        setupViewPager(viewPager2);
    }

    private void setupViewPager(ViewPager2 viewPager2) {
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(dpToPx(4)));
        transformer.addTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setScaleX(0.85f + (1 - absPos) * 0.15f);
            page.setScaleY(0.90f + (1 - absPos) * 0.10f);
            page.setAlpha(0.65f + (1 - absPos) * 0.35f);

            // Se elimina el cambio de elevación dinámico para mantener una sombra única y constante.
            // Esto soluciona la inconsistencia visual al iniciar y al deslizar.
            View cardContainer = page.findViewById(R.id.cardContainer);
            if (cardContainer instanceof MaterialCardView) {
                ((MaterialCardView) cardContainer).setCardElevation(dpToPx(8)); // Sombra constante
            }
        });
        viewPager2.setPageTransformer(transformer);

        viewPager2.post(() -> {
            if (viewPager2.getChildCount() > 0) {
                View child = viewPager2.getChildAt(0);
                if (child instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) child;
                    recyclerView.setClipToPadding(false);
                    recyclerView.setClipChildren(false);
                    recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                    recyclerView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isSkeleton() ? VIEW_TYPE_SKELETON : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_SKELETON) {
            return new SkeletonViewHolder(ItemBannerSkeletonBinding.inflate(inflater, parent, false));
        } else {
            return new BannerViewHolder(ItemSliderBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ((BannerViewHolder) holder).bind(getItem(position), clickListener);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    // --- ViewHolders ---

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemSliderBinding binding;

        public BannerViewHolder(@NonNull ItemSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Banner banner, OnBannerClickListener listener) {
            Glide.with(itemView.getContext())
                    .load(banner.getUrl())
                    .centerCrop()
                    .placeholder(R.color.colorGrey400)
                    .error(R.color.colorGrey400)
                    .into(binding.imageSlide);

            itemView.setOnClickListener(v -> {
                if (listener != null && !banner.isSkeleton()) {
                    listener.onBannerClick(banner);
                }
            });
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        public SkeletonViewHolder(ItemBannerSkeletonBinding binding) {
            super(binding.getRoot());
        }
    }

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }
}

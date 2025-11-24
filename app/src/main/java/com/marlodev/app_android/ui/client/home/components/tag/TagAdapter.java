package com.marlodev.app_android.ui.client.home.components.tag;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.databinding.ItemTagBinding;
import com.marlodev.app_android.databinding.ItemTagSkeletonBinding;
import com.marlodev.app_android.domain.model.Tag;

public class TagAdapter extends ListAdapter<Tag, RecyclerView.ViewHolder> {

    private OnTagClickListener listener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SKELETON = 1;

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }

    public TagAdapter() {
        super(new TagDiffCallback());
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
            ItemTagSkeletonBinding skeletonBinding = ItemTagSkeletonBinding.inflate(inflater, parent, false);
            return new SkeletonViewHolder(skeletonBinding);
        } else {
            ItemTagBinding binding = ItemTagBinding.inflate(inflater, parent, false);
            return new TagViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            TagViewHolder tagViewHolder = (TagViewHolder) holder;
            tagViewHolder.bind(getItem(position), listener);
        }
    }

    // --- ViewHolders ---

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        private final ItemTagBinding binding;

        public TagViewHolder(@NonNull ItemTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Tag tag, OnTagClickListener listener) {
            binding.tagTitle.setText(tag.getName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTagClick(tag);
                }
            });
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        public SkeletonViewHolder(ItemTagSkeletonBinding binding) {
            super(binding.getRoot());
        }
    }
}

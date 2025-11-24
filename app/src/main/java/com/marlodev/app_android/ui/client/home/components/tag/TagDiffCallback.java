package com.marlodev.app_android.ui.client.home.components.tag;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.marlodev.app_android.domain.model.Tag;

import java.util.Objects;

/**
 * DiffUtil.ItemCallback profesional para el modelo Tag.
 */
public class TagDiffCallback extends DiffUtil.ItemCallback<Tag> {

    @Override
    public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
        if (oldItem.isSkeleton() && newItem.isSkeleton()) {
            return true;
        }
        if (oldItem.isSkeleton() || newItem.isSkeleton()) {
            return false;
        }
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
        if (oldItem.isSkeleton() && newItem.isSkeleton()) {
            return true;
        }
        // CORREGIDO: La variable es oldItem, no old.
        return oldItem.equals(newItem);
    }
}

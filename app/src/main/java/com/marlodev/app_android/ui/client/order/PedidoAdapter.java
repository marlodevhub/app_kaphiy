package com.marlodev.app_android.ui.client.order;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.databinding.ItemPedidoHomeBinding;
import com.marlodev.app_android.databinding.ItemPedidoHomeSkeletonBinding;
import com.marlodev.app_android.domain.model.Pedido;

public class PedidoAdapter extends ListAdapter<Pedido, RecyclerView.ViewHolder> {

    private final OnPedidoClickListener listener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SKELETON = 1;

    public interface OnPedidoClickListener {
        void onAceptarClick(Pedido pedido);
        void onDetalleClick(Pedido pedido);
    }

    public PedidoAdapter(OnPedidoClickListener listener) {
        super(new PedidoDiffCallback());
        this.listener = listener;
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
            ItemPedidoHomeSkeletonBinding skeletonBinding = ItemPedidoHomeSkeletonBinding.inflate(inflater, parent, false);
            return new SkeletonViewHolder(skeletonBinding);
        } else {
            ItemPedidoHomeBinding binding = ItemPedidoHomeBinding.inflate(inflater, parent, false);
            return new PedidoViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            PedidoViewHolder pedidoViewHolder = (PedidoViewHolder) holder;
            pedidoViewHolder.bind(getItem(position), listener);
        }
        // No se necesita binding para el SkeletonViewHolder
    }

    // --- ViewHolders ---

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private final ItemPedidoHomeBinding binding;

        public PedidoViewHolder(ItemPedidoHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pedido pedido, OnPedidoClickListener listener) {
            binding.txtCliente.setText("Cliente: " + pedido.getCliente());
            binding.txtDireccion.setText(pedido.getDireccion());
            binding.txtDistancia.setText("Distancia: " + pedido.getDistancia());

            binding.btnAceptar.setOnClickListener(v -> {
                if (listener != null) listener.onAceptarClick(pedido);
            });

            binding.btnDetalle.setOnClickListener(v -> {
                if (listener != null) listener.onDetalleClick(pedido);
            });
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        // No se necesita lógica, solo mantiene la vista del esqueleto
        public SkeletonViewHolder(ItemPedidoHomeSkeletonBinding binding) {
            super(binding.getRoot());
        }
    }
}

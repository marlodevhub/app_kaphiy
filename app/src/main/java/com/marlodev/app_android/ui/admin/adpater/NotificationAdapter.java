package com.marlodev.app_android.ui.admin.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {

    private List<NotificationItem> lista;

    public NotificationAdapter(List<NotificationItem> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ Usa el layout correcto
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_notification, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        NotificationItem item = lista.get(position);

        holder.tvMensaje.setText(item.getMensaje());
        holder.tvFecha.setText(item.getFecha());

        // ✅ Asegúrate que estos drawables existan en res/drawable
        switch (item.getTipo()) {
            case "pedido":
                holder.imgIcono.setImageResource(R.drawable.ic_cart);
                break;
            case "stock":
                holder.imgIcono.setImageResource(R.drawable.ic_warning);
                break;
            case "info":
                holder.imgIcono.setImageResource(R.drawable.ic_info);
                break;
            case "error":
                holder.imgIcono.setImageResource(R.drawable.ic_error);
                break;
            default:
                holder.imgIcono.setImageResource(R.drawable.notify); // ícono genérico
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class NotiViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcono;
        TextView tvMensaje, tvFecha;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcono = itemView.findViewById(R.id.imgIcono);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
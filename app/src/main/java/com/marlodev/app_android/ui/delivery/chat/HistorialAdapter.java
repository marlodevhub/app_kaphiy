package com.marlodev.app_android.ui.delivery.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.Pedido;

import java.util.ArrayList;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<Pedido> lista = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido p = lista.get(position);

        holder.txtNombre.setText(p.getCliente());
        holder.txtFecha.setText(p.getFechaEntrega());
        holder.txtMonto.setText("Monto: S/ " + p.getTotal());
        holder.txtPropina.setText("Propina: S/ " + p.getPropina());
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public void submitList(List<Pedido> newList) {
        lista = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtFecha, txtMonto, txtPropina;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCliente);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtMonto = itemView.findViewById(R.id.txtMonto);
            txtPropina = itemView.findViewById(R.id.txtPropina);
        }
    }
}

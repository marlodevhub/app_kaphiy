package com.marlodev.app_android.ui.client.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.PedidoEntregado;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private final List<PedidoEntregado> pedidos;
    private final SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat formatoDia = new SimpleDateFormat("dd", Locale.getDefault());
    private final SimpleDateFormat formatoMes = new SimpleDateFormat("MMM", new Locale("es", "ES"));

    public HistorialAdapter(List<PedidoEntregado> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_entregado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidoEntregado pedido = pedidos.get(position);

        // Extraer día y mes de la fecha
        try {
            Date fecha = formatoEntrada.parse(pedido.getFecha());
            if (fecha != null) {
                holder.txtDia.setText(formatoDia.format(fecha));
                holder.txtMes.setText(formatoMes.format(fecha).toUpperCase());
            }
        } catch (ParseException e) {
            holder.txtDia.setText("--");
            holder.txtMes.setText("--");
        }

        // Mostrar hora
        holder.txtHora.setText(pedido.getHora());

        // Mostrar precio y propina con formato
        holder.txtPrecio.setText(String.format(Locale.getDefault(), "S/ %.2f", pedido.getPrecio()));
        holder.txtPropina.setText(String.format(Locale.getDefault(), "S/ %.2f", pedido.getPropina()));

        // Mostrar cliente
        holder.txtCliente.setText("Cliente: " + pedido.getCliente());
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDia, txtHora, txtPrecio, txtMes, txtCliente, txtPropina;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDia = itemView.findViewById(R.id.txtDia);
            txtHora = itemView.findViewById(R.id.txtHora);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtMes = itemView.findViewById(R.id.txtMes);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtPropina = itemView.findViewById(R.id.txtPropina);
        }
    }
}

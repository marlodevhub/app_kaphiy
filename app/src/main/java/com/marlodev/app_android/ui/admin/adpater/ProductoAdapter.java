package com.marlodev.app_android.ui.admin.adpater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    // Interfaz para editar
    public interface OnEditClickListener {
        void onEditClick(int position, Producto producto);
    }

    // Interfaz para eliminar
    public interface OnDeleteClickListener {
        void onDeleteClick(int position, Producto producto);
    }

    // Constructor con ambos callbacks
    public ProductoAdapter(List<Producto> listaProductos,
                           OnEditClickListener editClickListener,
                           OnDeleteClickListener deleteClickListener) {
        this.listaProductos = listaProductos;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());
        holder.tvEstado.setText(producto.getEstado());
        holder.tvCantidad.setText(producto.getCantidad());
        holder.imgProducto.setImageResource(producto.getImagenResId());

        // Color dinámico para estado
        if (producto.getEstado().equalsIgnoreCase("Agotado")) {
            holder.tvEstado.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorRed));
        } else if (producto.getEstado().equalsIgnoreCase("Disponible")) {
            holder.tvEstado.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorGreen1));
        }

        // Botón editar
        holder.btnEditar.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(position, producto);
            }
        });

        // Botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position, producto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvEstado, tvCantidad;
        ImageView imgProducto, btnEditar, btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProducto);
            tvEstado = itemView.findViewById(R.id.tvEstadoProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidadProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
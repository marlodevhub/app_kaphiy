package com.marlodev.app_android.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.Producto;
import com.marlodev.app_android.ui.admin.adpater.ProductoAdapter;


import java.util.ArrayList;
import java.util.List;

public class AdminProductsFragment extends Fragment {

    private EditText etBuscarProductos;
    private Spinner spinnerCategorias, spinnerStock;
    private RecyclerView recyclerVariantes;
    private List<Producto> listaProductos;
    private ProductoAdapter adapter;

    // Launchers modernos para crear y editar productos
    private ActivityResultLauncher<Intent> nuevoProductoLauncher;
    private ActivityResultLauncher<Intent> editarProductoLauncher;

    public AdminProductsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_products, container, false);

        etBuscarProductos = view.findViewById(R.id.etBuscarProductos);
        spinnerCategorias = view.findViewById(R.id.spinnerCategorias);
        spinnerStock = view.findViewById(R.id.spinnerStock);
        recyclerVariantes = view.findViewById(R.id.recycler_variantes);
        recyclerVariantes.setLayoutManager(new LinearLayoutManager(requireContext()));


        FrameLayout btnPerfilUsuario = view.findViewById(R.id.btnPerfilUsuario);
        btnPerfilUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UserAdminActivity.class);
            startActivity(intent);
        });



        FrameLayout btnNotificaciones = view.findViewById(R.id.btnNotificaciones);
        TextView badgeNumero = view.findViewById(R.id.badgeNumero);

// Simulación de lista de notificaciones
        ArrayList<String> listaNotificaciones = new ArrayList<>();
        listaNotificaciones.add("Nuevo pedido recibido");
        listaNotificaciones.add("Stock bajo en Capuccino Grande");
        listaNotificaciones.add("Producto editado exitosamente");

        int cantidad = listaNotificaciones.size();
        badgeNumero.setText(String.valueOf(cantidad));
        badgeNumero.setVisibility(cantidad == 0 ? View.GONE : View.VISIBLE);

        btnNotificaciones.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(requireContext(), NotificationsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error al abrir notificaciones: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });








        // Inicializar lista y adapter
        listaProductos = new ArrayList<>();
        adapter = new ProductoAdapter(listaProductos, this::editarProducto, this::confirmarEliminacion); // callback para editar y eliminar
        recyclerVariantes.setAdapter(adapter);

        // Launcher para recibir datos de NewProductActivity
        nuevoProductoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String nombre = data.getStringExtra("nombre");
                        String descripcion = data.getStringExtra("descripcion");
                        String categoria = data.getStringExtra("categoria");
                        String precioNormal = data.getStringExtra("precioNormal");
                        String precioOferta = data.getStringExtra("precioOferta");
                        String stockInicial = data.getStringExtra("stockInicial");
                        String stockMinimo = data.getStringExtra("stockMinimo");

                        Producto nuevoProducto = new Producto(
                                nombre,
                                descripcion,
                                "Disponible",
                                stockInicial + "/" + stockMinimo,
                                R.drawable.default_image
                        );

                        listaProductos.add(nuevoProducto);
                        adapter.notifyItemInserted(listaProductos.size() - 1);
                        recyclerVariantes.setVisibility(View.VISIBLE);
                    }
                }
        );

        // Launcher para recibir datos de EditProductActivity
        editarProductoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        int position = data.getIntExtra("position", -1);
                        if (position != -1) {
                            String nombre = data.getStringExtra("nombre");
                            String descripcion = data.getStringExtra("descripcion");
                            String categoria = data.getStringExtra("categoria");
                            String precioNormal = data.getStringExtra("precioNormal");
                            String precioOferta = data.getStringExtra("precioOferta");
                            String stockInicial = data.getStringExtra("stockInicial");
                            String stockMinimo = data.getStringExtra("stockMinimo");

                            Producto productoEditado = listaProductos.get(position);
                            productoEditado.setNombre(nombre);
                            productoEditado.setDescripcion(descripcion);
                            productoEditado.setEstado("Disponible");
                            productoEditado.setCantidad(stockInicial + "/" + stockMinimo);

                            adapter.notifyItemChanged(position);
                        }
                    }
                }
        );

        // Botón para nuevo producto
        Button btnNuevoProducto = view.findViewById(R.id.btnNuevoProducto);
        btnNuevoProducto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewProductActivity.class);
            nuevoProductoLauncher.launch(intent);
        });

        // Spinner categorías
        String[] categorias = {"Todas las categorías", "Café", "Panes", "Frappes", "Postres"};
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categorias
        );
        spinnerCategorias.setAdapter(adapterCategorias);

        // Spinner stock
        String[] opcionesStock = {"Todo el stock", "Disponible", "Agotado", "Próximamente"};
        ArrayAdapter<String> adapterStock = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                opcionesStock
        );
        spinnerStock.setAdapter(adapterStock);

        // Buscar productos
        etBuscarProductos.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { verificarCampos(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                verificarCampos();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                verificarCampos();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void verificarCampos() {
        String texto = etBuscarProductos.getText().toString().trim().toLowerCase();
        String categoria = spinnerCategorias.getSelectedItem().toString();
        String stock = spinnerStock.getSelectedItem().toString();

        boolean textoValido = !texto.isEmpty();
        boolean categoriaValida = !categoria.equals("Todas las categorías");
        boolean stockValido = !stock.equals("Todo el stock");

        if (textoValido && categoriaValida && stockValido) {
            recyclerVariantes.setVisibility(View.VISIBLE);

            List<Producto> productosFiltrados = new ArrayList<>();

            if (texto.contains("capuchino") && categoria.equals("Café") && stock.equals("Disponible")) {
                productosFiltrados.add(new Producto(
                        "Capuchino Grande",
                        "Café espresso con leche y espuma cremosa",
                        "Disponible",
                        "20/80",
                        R.drawable.capuccino
                ));
            }

            if (texto.contains("croissant") && categoria.equals("Panes") && stock.equals("Agotado")) {
                productosFiltrados.add(new Producto(
                        "Croissant de mantequilla",
                        "Pan francés horneado con mantequilla",
                        "Agotado",
                        "50/65",
                        R.drawable.croissant
                ));
            }

            adapter = new ProductoAdapter(productosFiltrados, this::editarProducto, this::confirmarEliminacion);
            recyclerVariantes.setAdapter(adapter);
        } else {
            recyclerVariantes.setVisibility(View.GONE);
        }
    }

    // Método para lanzar edición
    private void editarProducto(int position, Producto producto) {
        Intent intent = new Intent(getActivity(), EditProductActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("nombre", producto.getNombre());
        intent.putExtra("descripcion", producto.getDescripcion());
        intent.putExtra("estado", producto.getEstado());
        intent.putExtra("cantidad", producto.getCantidad());
        intent.putExtra("imagen", producto.getImagenResId());
        editarProductoLauncher.launch(intent);
    }

    private void confirmarEliminacion(int position, Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView tvMensaje = dialogView.findViewById(R.id.tvMensajeEliminar);
        tvMensaje.setText("¿Estás seguro de que deseas eliminar el producto \"" + producto.getNombre() + "\"?");

        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarEliminar);
        Button btnEliminar = dialogView.findViewById(R.id.btnConfirmarEliminar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnEliminar.setOnClickListener(v -> {
            listaProductos.remove(position);
            adapter.notifyItemRemoved(position);
            dialog.dismiss();
        });

        dialog.show();
    }


}
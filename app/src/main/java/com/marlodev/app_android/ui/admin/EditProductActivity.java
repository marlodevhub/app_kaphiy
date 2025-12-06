package com.marlodev.app_android.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.marlodev.app_android.R;

public class EditProductActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDescripcion, etPrecioNormal, etPrecioOferta, etStockInicial, etStockMinimo;
    private Spinner spinnerCategoria;
    private Button btnCancelar, btnGuardar;

    private int position; // posición del producto en la lista

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Recuperar datos enviados desde el Adapter/Fragment
        position = getIntent().getIntExtra("position", -1);
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        String estado = getIntent().getStringExtra("estado");
        String cantidad = getIntent().getStringExtra("cantidad");
        int imagenResId = getIntent().getIntExtra("imagen", R.drawable.default_image);

        // Conectar vistas con sus IDs
        etNombre = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcionProducto);
        etPrecioNormal = findViewById(R.id.etPrecioNormal);
        etPrecioOferta = findViewById(R.id.etPrecioOferta);
        etStockInicial = findViewById(R.id.etStockInicial);
        etStockMinimo = findViewById(R.id.etStockMinimo);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        btnCancelar = findViewById(R.id.btnCancelarEdicion);
        btnGuardar = findViewById(R.id.btnGuardarEdicion);

        // Asignar valores recibidos a los campos
        etNombre.setText(nombre);
        etDescripcion.setText(descripcion);
        etStockInicial.setText(cantidad); // ejemplo: puedes usar cantidad como stock inicial

        // Configurar spinner con categorías
        String[] categorias = {"Café", "Panes", "Frappes", "Postres"};
        android.widget.ArrayAdapter<String> adapterCategorias = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categorias
        );
        spinnerCategoria.setAdapter(adapterCategorias);

        // Seleccionar automáticamente la categoría (ejemplo: siempre "Panes")
        int pos = adapterCategorias.getPosition("Panes");
        if (pos >= 0) spinnerCategoria.setSelection(pos);

        // Botón cancelar → cerrar actividad
        btnCancelar.setOnClickListener(v -> finish());

        // Botón guardar → devolver datos editados al Fragment
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevaDescripcion = etDescripcion.getText().toString().trim();
            String nuevaCategoria = spinnerCategoria.getSelectedItem().toString();
            String nuevoPrecioNormal = etPrecioNormal.getText().toString().trim();
            String nuevoPrecioOferta = etPrecioOferta.getText().toString().trim();
            String nuevoStockInicial = etStockInicial.getText().toString().trim();
            String nuevoStockMinimo = etStockMinimo.getText().toString().trim();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("position", position); // 🔑 posición del producto
            resultIntent.putExtra("nombre", nuevoNombre);
            resultIntent.putExtra("descripcion", nuevaDescripcion);
            resultIntent.putExtra("categoria", nuevaCategoria);
            resultIntent.putExtra("precioNormal", nuevoPrecioNormal);
            resultIntent.putExtra("precioOferta", nuevoPrecioOferta);
            resultIntent.putExtra("stockInicial", nuevoStockInicial);
            resultIntent.putExtra("stockMinimo", nuevoStockMinimo);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
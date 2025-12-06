package com.marlodev.app_android.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.marlodev.app_android.R;

public class NewProductActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDescripcion, etPrecioNormal, etPrecioOferta, etStockInicial, etStockMinimo;
    private Spinner spinnerCategoria;
    private Button btnCancelar, btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_product);

        // Conectar vistas con sus IDs del XML
        etNombre = findViewById(R.id.etNombreNuevo);
        etDescripcion = findViewById(R.id.etDescripcionNuevo);
        etPrecioNormal = findViewById(R.id.etPrecioNormalNuevo);
        etPrecioOferta = findViewById(R.id.etPrecioOfertaNuevo);
        etStockInicial = findViewById(R.id.etStockInicialNuevo);
        etStockMinimo = findViewById(R.id.etStockMinimoNuevo);
        spinnerCategoria = findViewById(R.id.spinnerCategoriaNuevo);
        btnCancelar = findViewById(R.id.btnCancelarNuevo);
        btnCrear = findViewById(R.id.btnCrearNuevo);

        // Botón cancelar → cerrar actividad
        btnCancelar.setOnClickListener(v -> finish());

        // Botón crear → recoger datos y devolverlos al fragmento
        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String categoria = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "";
            String precioNormal = etPrecioNormal.getText().toString().trim();
            String precioOferta = etPrecioOferta.getText().toString().trim();
            String stockInicial = etStockInicial.getText().toString().trim();
            String stockMinimo = etStockMinimo.getText().toString().trim();

            // Crear intent con datos
            Intent resultIntent = new Intent();
            resultIntent.putExtra("nombre", nombre);
            resultIntent.putExtra("descripcion", descripcion);
            resultIntent.putExtra("categoria", categoria);
            resultIntent.putExtra("precioNormal", precioNormal);
            resultIntent.putExtra("precioOferta", precioOferta);
            resultIntent.putExtra("stockInicial", stockInicial);
            resultIntent.putExtra("stockMinimo", stockMinimo);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
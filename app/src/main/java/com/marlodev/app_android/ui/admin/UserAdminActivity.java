package com.marlodev.app_android.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.marlodev.app_android.R;

public class UserAdminActivity extends AppCompatActivity {

    private ImageView imgPerfilUsuario;
    private TextView tvNombreUsuario, tvCorreoUsuario, tvRolUsuario;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin);

        imgPerfilUsuario = findViewById(R.id.imgPerfilUsuario);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = findViewById(R.id.tvCorreoUsuario);
        tvRolUsuario = findViewById(R.id.tvRolUsuario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Cargar datos (puedes hacerlo dinámico si tienes sesión)
        tvNombreUsuario.setText("Nicol");
        tvCorreoUsuario.setText("nicol@example.com");
        tvRolUsuario.setText("Administradora");

        btnCerrarSesion.setOnClickListener(v -> finish()); // o cerrar sesión real
    }
}
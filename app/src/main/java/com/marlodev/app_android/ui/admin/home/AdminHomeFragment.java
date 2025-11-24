package com.marlodev.app_android.ui.admin.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.utils.SessionManager;

public class AdminHomeFragment extends Fragment {
    private TextView tvUserName, tvUserEmail, tvUserRole;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflamos el layout del fragment
        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Inicializamos views
        initViews(root);

        // Edge-to-edge (opcional, igual que en la Activity)
        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargamos datos del usuario
        loadUserData();

        // Configuramos el botón de logout
        setupLogoutButton();

        return root;
    }

    private void initViews(View root) {
        tvUserName = root.findViewById(R.id.tvUserName);
        tvUserEmail = root.findViewById(R.id.tvUserEmail);
        tvUserRole = root.findViewById(R.id.tvUserRole);
        btnLogout = root.findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        SessionManager session = SessionManager.getInstance(requireContext());
        String email = session.getEmail();
        String role = session.getRole();

        // Nombre simulado a partir del correo
        String username = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf("@"))
                : "Delivery";

        tvUserName.setText(username);
        tvUserEmail.setText(email != null ? email : "correo@ejemplo.com");
        tvUserRole.setText("Rol: " + (role != null ? role : "Desconocido"));
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            // Borrar sesión
            SessionManager.getInstance(requireContext()).clear();

            // Redirigir a MainActivity
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
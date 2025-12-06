package com.marlodev.app_android.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // <-- Importante

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Importaciones para los componentes de Material Design
import com.google.android.material.textfield.TextInputLayout; // <-- Importante

import com.marlodev.app_android.R;

public class AdminUsersFragment extends Fragment {

    public AdminUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    // --- ESTE MÉTODO ES EL QUE HACE EL TRABAJO ---
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- ENCONTRAR LOS DOS COMPONENTES POR SUS IDs ---

        // 1. El layout de texto para "Buscar usuarios"
        TextInputLayout searchLayout = view.findViewById(R.id.search_text_input_layout);

        // 2. El recuadro de texto para "Todos los roles"
        TextView rolesFilter = view.findViewById(R.id.roles_filter_text);

        // --- CAMBIAR EL COLOR DE AMBOS A GRIS CLARO ---

        // Definimos el color gris una sola vez
        int lightGreyColor = Color.parseColor("#F5F5F5");

        // Aplicamos el color al fondo del campo de búsqueda
        if (searchLayout != null) {
            // Esta es la línea clave: cambiamos el color de la "caja" del TextInputLayout
            searchLayout.setBoxBackgroundColor(lightGreyColor);
        }

        // Aplicamos el color al fondo del filtro de roles
        if (rolesFilter != null) {
            // Para cambiar el fondo de un TextView que usa un drawable, le cambiamos el tinte.
            // Esto conserva la forma (bordes redondeados) pero cambia el color.
        }
    }
}

package com.marlodev.app_android.ui.barista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.databinding.FragmentBaristaPerfilBinding;
import com.marlodev.app_android.utils.SessionManager;

public class BaristaPerfilFragment extends Fragment {

    private FragmentBaristaPerfilBinding binding;

    public BaristaPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // INFLA el binding correcto para el layout fragment_barista_perfil.xml
        binding = FragmentBaristaPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Llamamos la inicialización de UI sólo después de que la vista esté creada
        loadUserData();
        setupLogoutButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberar referencia al binding para evitar fugas y NPE posteriores
        binding = null;
    }

    // -------------------------------
    //  VINCULAR DATOS DEL USUARIO
    // -------------------------------
    private void loadUserData() {
        // Protección por si alguien llama este método después de onDestroyView
        if (binding == null || !isAdded()) return;

        SessionManager session = SessionManager.getInstance(requireContext());

        String email = session.getEmail();
        String role = session.getRole();

        // Obtener nombre a partir del email
        String username = "Usuario";
        if (email != null && email.contains("@")) {
            username = email.substring(0, email.indexOf("@"));
        }

        binding.tvUserName.setText(username);
        binding.tvEmail.setText(email != null ? email : "No disponible");
        binding.tvRole.setText(role != null ? "Rol: " + role : "Rol: Invitado");

        // Si tienes avatar y usas Glide o similar:
        // String avatarUrl = session.getAvatarUrl();
        // if (avatarUrl != null && !avatarUrl.isEmpty()) {
        //     Glide.with(requireContext()).load(avatarUrl).into(binding.ivUserAvatar);
        // }
    }

    // -------------------------------
    //            LOGOUT
    // -------------------------------
    private void setupLogoutButton() {
        if (binding == null) return;

        binding.btnLogout.setOnClickListener(v -> {
            SessionManager session = SessionManager.getInstance(requireContext());
            session.clear(); // Limpia email, token, role, userId, etc.

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Termine la actividad anfitriona por seguridad
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}

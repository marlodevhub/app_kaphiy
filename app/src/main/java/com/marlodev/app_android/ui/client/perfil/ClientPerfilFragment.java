package com.marlodev.app_android.ui.client.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.databinding.FragmentClientPerfilBinding;
import com.marlodev.app_android.utils.SessionManager;

public class ClientPerfilFragment extends Fragment {

    private FragmentClientPerfilBinding binding;

    public ClientPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClientPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lógica de UI simplificada. Toda la complejidad ha sido eliminada.

        loadUserData();
        setupLogoutButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevenir fugas de memoria
    }

    private void loadUserData() {
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();

        String username = "Usuario"; // Valor por defecto
        if (email != null && email.contains("@")) {
            username = email.substring(0, email.indexOf("@"));
        }

        binding.tvUserName.setText(username);
        binding.tvEmail.setText(email != null ? email : "No disponible");
        binding.tvRole.setText(String.format("Rol: %s", role != null ? role : "Invitado"));
    }

    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clear();
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}

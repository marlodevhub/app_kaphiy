package com.marlodev.app_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.ui.admin.AdminMainActivity;
import com.marlodev.app_android.ui.auth.login.LoginActivity;
import com.marlodev.app_android.ui.client.cart.ClientCarFragment;
import com.marlodev.app_android.ui.client.home.ClientHomeFragment;
import com.marlodev.app_android.ui.client.order.ClientOrderFragment;
import com.marlodev.app_android.ui.client.perfil.ClientPerfilFragment;
import com.marlodev.app_android.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        // --- ARQUITECTURA LIMPIA Y PROFESIONAL ---
        // La apariencia de la barra de estado ahora es controlada 100% por el tema (themes.xml).
        // Esta Activity ya no contiene lógica de UI del sistema.
        // --- FIN DE LA ARQUITECTURA BASE ---

        if (checkSessionAndRedirect()) {
            return;
        }

        setupBottomNavigation();
    }

    private boolean checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ("ADMIN".equalsIgnoreCase(sessionManager.getRole())) {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            return true;
        }

        return false;
    }

    private void setupBottomNavigation() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            binding.bottomNavigation.setItemSelected(R.id.menu_home, true);
            replaceFragment(new ClientHomeFragment());
        }
        binding.bottomNavigation.setOnItemSelectedListener(this::handleNavigationItemSelected);
    }

    private void handleNavigationItemSelected(int id) {
        Fragment selectedFragment = null;
        if (id == R.id.menu_home) {
            selectedFragment = new ClientHomeFragment();
        } else if (id == R.id.menu_car) {
            selectedFragment = new ClientCarFragment();
        } else if (id == R.id.menu_delivery) {
            selectedFragment = new ClientOrderFragment();
        } else if (id == R.id.menu_perfil) {
            openProfileOrLogin();
            return;
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    private void openProfileOrLogin() {
        if (sessionManager.isLoggedIn()) {
            replaceFragment(new ClientPerfilFragment());
        } else {
            Toast.makeText(this, "Inicia sesión para ver tu perfil", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

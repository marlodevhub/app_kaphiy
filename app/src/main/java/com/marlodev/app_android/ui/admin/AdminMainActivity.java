package com.marlodev.app_android.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.admin.home.AdminHomeFragment;
import com.marlodev.app_android.ui.auth.login.LoginActivity;
import com.marlodev.app_android.utils.SessionManager;

public class AdminMainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ChipNavigationBar bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        sessionManager = SessionManager.getInstance(this);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 🔷 Ajuste de padding para dispositivos con notch o barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkSessionAndRedirect();
        setupBottomNavigation();
    }

    /**
     * Configura la barra de navegación inferior.
     * Define el fragmento inicial y los listeners de los menús.
     */

    private void setupBottomNavigation() {
        bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorGrey100));

        // Fragmento por defecto al iniciar
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            loadFragment(new AdminHomeFragment());
            bottomNavigation.setItemSelected(R.id.menu_home_admin, true);
        }

        // Escucha de selección de ítems en el menú
        bottomNavigation.setOnItemSelectedListener(id -> {
            Fragment fragment = null;

            if (id == R.id.menu_home_admin) {
                fragment = new AdminHomeFragment();
            } else if (id == R.id.menu_productos_admin) {
                fragment = new AdminProductsFragment();
            } else if (id == R.id.menu_usuarios_admin) {
                fragment = new AdminUsersFragment();
            } else if (id == R.id.menu_tags_admin) {
                fragment = new AdminTagsFragment();
            } else if (id == R.id.menu_inventario_admin) {
                openProfileOrGuest();
                return;
            }
            if (fragment != null) loadFragment(fragment);
        });
    }

    // Muestra mensaje de bienvenida si el usuario no está logueado.
    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
        }
    }

    // Abre el perfil si hay sesión, o redirige al login si es invitado.
    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            loadFragment(new AdminInventarioFragment());
        } else {
            Toast.makeText(this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    // Metodo auxiliar para cargar fragmentos.
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}

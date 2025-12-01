package com.marlodev.app_android.ui.barista;

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
import com.marlodev.app_android.ui.auth.login.LoginActivity;
import com.marlodev.app_android.ui.barista.historial.BaristaHistorialFragment;
import com.marlodev.app_android.ui.barista.home.BaristaHomeFragment;
import com.marlodev.app_android.ui.barista.inventario.BaristaInventarioFragment;
import com.marlodev.app_android.ui.barista.ordenes.BaristaOrderFragment;
import com.marlodev.app_android.ui.barista.perfil.BaristaPerfilFragment;
import com.marlodev.app_android.utils.SessionManager;

public class BaristaMainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ChipNavigationBar bottomNavigation;

    // Fragments persistentes
    private Fragment homeFragment;
    private Fragment ordersFragment;
    private Fragment historialFragment;
    private Fragment inventarioFragment;
    private Fragment perfilFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista_main);

        sessionManager = SessionManager.getInstance(this);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Ajuste de padding para notch/barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkSessionAndRedirect();
        setupFragments();
        setupBottomNavigation();
    }

    /** Inicializa fragments y los mantiene persistentes */
    private void setupFragments() {
        homeFragment = new BaristaHomeFragment();
        ordersFragment = new BaristaOrderFragment();
        historialFragment = new BaristaHistorialFragment();
        inventarioFragment = new BaristaInventarioFragment();
        perfilFragment = new BaristaPerfilFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment, "HOME")
                .add(R.id.fragment_container, ordersFragment, "ORDERS").hide(ordersFragment)
                .add(R.id.fragment_container, historialFragment, "HISTORIAL").hide(historialFragment)
                .add(R.id.fragment_container, inventarioFragment, "INVENTARIO").hide(inventarioFragment)
                .add(R.id.fragment_container, perfilFragment, "PERFIL").hide(perfilFragment)
                .commit();

        activeFragment = homeFragment;
    }

    /** Configura bottom navigation */
    private void setupBottomNavigation() {
        bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorGrey100));
        bottomNavigation.setItemSelected(R.id.menu_home_barista, true);

        bottomNavigation.setOnItemSelectedListener(id -> {
            Fragment target = null;

            if (id == R.id.menu_home_barista) target = homeFragment;
            else if (id == R.id.menu_ordenes_barista) target = ordersFragment;
            else if (id == R.id.menu_historial_barista) target = historialFragment;
            else if (id == R.id.menu_inventario_barista) target = inventarioFragment;
            else if (id == R.id.menu_perfil_barista) {
                openProfileOrGuest();
                return;
            }

            if (target != null && target != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(activeFragment)
                        .show(target)
                        .commit();
                activeFragment = target;
            }
        });
    }

    /** Comprueba sesión y muestra mensaje de invitado */
    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
        }
    }

    /** Abre perfil o login sin destruir fragments */
    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(perfilFragment)
                    .commit();
            activeFragment = perfilFragment;
        } else {
            Toast.makeText(this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

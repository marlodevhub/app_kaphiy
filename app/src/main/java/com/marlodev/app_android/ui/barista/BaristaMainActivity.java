package com.marlodev.app_android.ui.barista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.R;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.ui.auth.login.LoginActivity;
import com.marlodev.app_android.ui.barista.historial.BaristaHistorialFragment;
import com.marlodev.app_android.ui.barista.home.BaristaHomeFragment;
import com.marlodev.app_android.ui.barista.inventario.BaristaInventarioFragment;
import com.marlodev.app_android.ui.barista.ordenes.BaristaOrdenesViewModelFactory;
import com.marlodev.app_android.ui.barista.ordenes.BaristaOrderFragment;
import com.marlodev.app_android.ui.barista.perfil.BaristaPerfilFragment;
import com.marlodev.app_android.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class BaristaMainActivity extends AppCompatActivity {


    private ChipNavigationBar bottomNavigation;

    private Fragment activeFragment;
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista_main);

        sessionManager = SessionManager.getInstance(this);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Obtener los casos de uso desde tu container
        AppContainer container = ((MainApplication) getApplication()).appContainer;


        setupFragments();
        setupBottomNavigation();
    }


    /** Inicializa y agrega los fragments al container */
    private void setupFragments() {
        Fragment homeFragment = new BaristaHomeFragment();
        Fragment ordersFragment = new BaristaOrderFragment();
        Fragment historialFragment = new BaristaHistorialFragment();
        Fragment inventarioFragment = new BaristaInventarioFragment();
        Fragment perfilFragment = new BaristaPerfilFragment();

        fragmentMap.put(R.id.menu_home_barista, homeFragment);
        fragmentMap.put(R.id.menu_ordenes_barista, ordersFragment);
        fragmentMap.put(R.id.menu_historial_barista, historialFragment);
        fragmentMap.put(R.id.menu_inventario_barista, inventarioFragment);
        fragmentMap.put(R.id.menu_perfil_barista, perfilFragment);

        // Agregar todos los fragments, ocultando todos menos home
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment, "HOME")
                .add(R.id.fragment_container, ordersFragment, "ORDERS").hide(ordersFragment)
                .add(R.id.fragment_container, historialFragment, "HISTORIAL").hide(historialFragment)
                .add(R.id.fragment_container, inventarioFragment, "INVENTARIO").hide(inventarioFragment)
                .add(R.id.fragment_container, perfilFragment, "PERFIL").hide(perfilFragment)
                .commit();

        activeFragment = homeFragment;
    }

    /** Configura navegación inferior usando Map */
    private void setupBottomNavigation() {
        bottomNavigation.setItemSelected(R.id.menu_home_barista, true);

        bottomNavigation.setOnItemSelectedListener(id -> {
            Fragment target = fragmentMap.get(id);

            if (target != null && target != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(activeFragment)
                        .show(target)
                        .commit();
                activeFragment = target;
            } else if (id == R.id.menu_perfil_barista) {
                openProfileOrGuest();
            }
        });
    }

    /** Abre perfil o Login según sesión */
    private void openProfileOrGuest() {
        Fragment perfilFragment = fragmentMap.get(R.id.menu_perfil_barista);
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

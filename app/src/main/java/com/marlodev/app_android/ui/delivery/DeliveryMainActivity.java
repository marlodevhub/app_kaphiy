package com.marlodev.app_android.ui.delivery;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.Pedido;

public class DeliveryMainActivity extends AppCompatActivity implements DeliveryHomeFragment.OnPedidoSelectedListener {

    private ChipNavigationBar bottomNavigation;

    // Fragmentos persistentes
    private Fragment homeFragment;
    private Fragment historialFragment;
    private Fragment gananciasFragment;
    private Fragment mensajeriaFragment;
    private Fragment perfilFragment;

    private Fragment activeFragment;
    private boolean detalleVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Inicialización de fragmentos persistentes
        homeFragment = new DeliveryHomeFragment();
        historialFragment = new DeliveryHistorialFragment();
        gananciasFragment = new DeliveryGananciasFragment();
        mensajeriaFragment = new DeliveryMensajeriaFragment();
        perfilFragment = new DeliveryPerfilFragment();

        // Agregamos todos los fragmentos pero mostramos solo Home
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, perfilFragment, "PERFIL").hide(perfilFragment)
                .add(R.id.fragment_container, mensajeriaFragment, "MENSAJERIA").hide(mensajeriaFragment)
                .add(R.id.fragment_container, gananciasFragment, "GANANCIAS").hide(gananciasFragment)
                .add(R.id.fragment_container, historialFragment, "HISTORIAL").hide(historialFragment)
                .add(R.id.fragment_container, homeFragment, "HOME")
                .commit();

        activeFragment = homeFragment;
        bottomNavigation.setItemSelected(R.id.menu_home_delivery, true);

        bottomNavigation.setOnItemSelectedListener(id -> {
            if (detalleVisible) {
                // Si está abierto el detalle, volvemos antes de cambiar de pestaña
                getSupportFragmentManager().popBackStack();
                detalleVisible = false;
            }

            Fragment fragmentToShow = null;
            if (id == R.id.menu_home_delivery) {
                fragmentToShow = homeFragment;
            } else if (id == R.id.menu_historial_delivery) {
                fragmentToShow = historialFragment;
            } else if (id == R.id.menu_ganancias_delivery) {
                fragmentToShow = gananciasFragment;
            } else if (id == R.id.menu_Mensajeria_delivery) {
                fragmentToShow = mensajeriaFragment;
            } else if (id == R.id.menu_perfil_delivery) {
                fragmentToShow = perfilFragment;
            }

            if (fragmentToShow != null && fragmentToShow != activeFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(activeFragment).show(fragmentToShow).commit();
                activeFragment = fragmentToShow;
            }
        });
    }

    @Override
    public void onPedidoSelected(Pedido pedido) {
        openDetallePedido(pedido);
    }

    private void openDetallePedido(Pedido pedido) {
        DeliveryHistorialFragment detalleFragment = new DeliveryHistorialFragment();
        Bundle args = new Bundle();
        args.putSerializable("pedido", pedido);
        detalleFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .add(R.id.fragment_container, detalleFragment, "DETALLE_PEDIDO")
                .addToBackStack("DETALLE_PEDIDO")
                .commit();

        detalleVisible = true;
    }

    @Override
    public void onBackPressed() {
        if (detalleVisible) {
            getSupportFragmentManager().popBackStack();
            detalleVisible = false;

            // Restaurar el fragmento activo actual
            getSupportFragmentManager().beginTransaction()
                    .show(activeFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }
}

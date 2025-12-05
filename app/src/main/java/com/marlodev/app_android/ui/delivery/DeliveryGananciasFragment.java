package com.marlodev.app_android.ui.delivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.PedidoEntregado;

import java.util.ArrayList;
import java.util.List;

public class DeliveryGananciasFragment extends Fragment {

    private TextView txtEntregas, txtGanancias, txtPropinas;
    private RecyclerView recyclerHistorial;
    private DetallePedido adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_ganancias, container, false);

        // Inicializar TextViews
        txtEntregas = view.findViewById(R.id.txtEntregas);
        txtGanancias = view.findViewById(R.id.txtGanancias);
        txtPropinas = view.findViewById(R.id.txtPropinas);

        // RecyclerView
        recyclerHistorial = view.findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos simulados
        List<PedidoEntregado> pedidos = cargarPedidosSimulados();
        adapter = new DetallePedido(pedidos);
        recyclerHistorial.setAdapter(adapter);

        // Calcular resumen
        calcularResumen(pedidos);

        return view;
    }

    private List<PedidoEntregado> cargarPedidosSimulados() {
        List<PedidoEntregado> lista = new ArrayList<>();
        lista.add(new PedidoEntregado("1", "12/11/2025", "16:30", "Noviembre 2025", "Benjamin RG", 25.50, 3.00));
        lista.add(new PedidoEntregado("2", "10/12/2025", "18:10", "Diciembre 2025", "Fany Nicol", 32.00, 2.50));
        lista.add(new PedidoEntregado("3", "20/09/2025", "20:00", "Agosto 2025", "Hugo Silva", 18.90, 0.00));
        return lista;
    }

    private void calcularResumen(List<PedidoEntregado> pedidos) {
        int totalEntregas = pedidos.size();
        double totalGanancias = 0;
        double totalPropinas = 0;

        for (PedidoEntregado p : pedidos) {
            totalGanancias += p.getPrecio();
            totalPropinas += p.getPropina();
        }

        txtEntregas.setText("" + totalEntregas);
        txtGanancias.setText("" + String.format("%.2f", totalGanancias));
        txtPropinas.setText("" + String.format("%.2f", totalPropinas));
    }
}

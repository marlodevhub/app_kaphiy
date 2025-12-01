package com.marlodev.app_android.ui.barista.ordenes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.ui.barista.ordenes.components.OrderCardBaristaAdapter;
import com.marlodev.app_android.domain.model.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaristaOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderCardBaristaAdapter adapter;
    private BaristaOrderViewModel viewModel;

    private Button btnPrevious, btnNext;
    private TextView tvPageInfo;

    private int currentPage = 0;
    private int totalPages = 1;
    private final int pageSize = 20;

    // Map para evitar duplicados y mantener referencia rápida
    private final Map<Long, Order> orderMap = new LinkedHashMap<>();
    // Lista visible para la página actual
    private final List<Order> visibleOrders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_barista_ordenes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycle_ordenes_espera);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderCardBaristaAdapter();
        recyclerView.setAdapter(adapter);

        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);
        tvPageInfo = view.findViewById(R.id.tv_page_info);

        AppContainer container = ((MainApplication) requireActivity().getApplication()).appContainer;
        viewModel = new ViewModelProvider(requireActivity(),
                new BaristaOrdenesViewModelFactory(container.orderUseCases))
                .get(BaristaOrderViewModel.class);

        btnPrevious.setOnClickListener(v -> loadPage(currentPage - 1));
        btnNext.setOnClickListener(v -> loadPage(currentPage + 1));

        // Observa WebSocket para pedidos en tiempo real
        observeWebSocket();

        // Cargar primera página REST
        loadPage(0);

        adapter.setOnItemClickListener(order -> viewModel.startPreparation(order.getId()));
    }

    private void observeWebSocket() {
        viewModel.getPendingOrders().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                boolean hasChanges = false;
                for (Order order : result.data) {
                    if (!orderMap.containsKey(order.getId())) {
                        orderMap.put(order.getId(), order); // nuevo pedido
                        hasChanges = true;
                    } else {
                        // actualizar pedido existente
                        Order old = orderMap.get(order.getId());
                        if (!order.equals(old)) {
                            orderMap.put(order.getId(), order);
                            hasChanges = true;
                        }
                    }
                }
                if (hasChanges) refreshVisibleOrders();
            }
        });
    }

    private void loadPage(int page) {
        if (page < 0 || page >= totalPages) return;

        viewModel.getBaristaOrdersPage(page, pageSize).observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                currentPage = result.data.number;
                totalPages = result.data.totalPages;

                // Guardamos todos los pedidos de la página en el map
                for (Order order : result.data.content) {
                    orderMap.put(order.getId(), order);
                }

                refreshVisibleOrders();

                tvPageInfo.setText("Página " + (currentPage + 1) + " / " + totalPages);
                btnPrevious.setEnabled(currentPage > 0);
                btnNext.setEnabled(currentPage < totalPages - 1);
            }
        });
    }

    private void refreshVisibleOrders() {
        // Tomar todos los pedidos del map y ordenarlos por fecha descendente
        List<Order> allOrders = new ArrayList<>(orderMap.values());
        allOrders.sort(Comparator.comparing(Order::getCreatedAt).reversed());

        // Limitar a los 20 pedidos visibles por página
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allOrders.size());
        visibleOrders.clear();
        if (startIndex < endIndex) {
            visibleOrders.addAll(allOrders.subList(startIndex, endIndex));
        }

        adapter.submitList(new ArrayList<>(visibleOrders));
    }
}

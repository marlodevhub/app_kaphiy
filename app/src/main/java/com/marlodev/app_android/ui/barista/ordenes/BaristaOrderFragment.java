package com.marlodev.app_android.ui.barista.ordenes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.ui.barista.ordenes.components.OrderCardBaristaAdapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BaristaOrderFragment extends Fragment {

    private BaristaOrderViewModel viewModel;
    private OrderCardBaristaAdapter adapter;
    private RecyclerView recyclerView;

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

        setupRecyclerView(view);
        setupViewModel();
        observePendingOrders();
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycle_ordenes_espera);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderCardBaristaAdapter(); // ✅ Usar constructor sin parámetros
        recyclerView.setAdapter(adapter);

        // Opcional: listener para botón "Preparar"
        adapter.setOnItemClickListener(order -> {
            // Aquí puedes llamar a tu ViewModel para iniciar preparación
            viewModel.startPreparation(order.getId());
        });
    }

    private void setupViewModel() {
        AppContainer container = ((MainApplication) requireActivity().getApplication()).appContainer;
        viewModel = new ViewModelProvider(this,
                new BaristaOrdenesViewModelFactory(container.orderUseCases))
                .get(BaristaOrderViewModel.class);
    }

//    private void observePendingOrders() {
//        viewModel.pendingOrders.observe(getViewLifecycleOwner(), result -> {
//            if (result != null && result.data != null && result.isSuccess()) {
//                adapter.submitList(result.data);
//            }
//        });
//    }
//
private void observePendingOrders() {
    viewModel.pendingOrders.observe(getViewLifecycleOwner(), result -> {
        if (result != null && result.data != null && result.isSuccess()) {

            List<Order> orders = new ArrayList<>(result.data);

            // Ordenar por confirmedAt, más recientes primero
            orders.sort((o1, o2) -> {
                ZonedDateTime t1 = o1.getConfirmedAt();
                ZonedDateTime t2 = o2.getConfirmedAt();

                if (t1 == null && t2 == null) return 0;
                if (t1 == null) return 1;
                if (t2 == null) return -1;

                return t2.compareTo(t1); // DESCENDENTE
            });

            adapter.submitList(orders);
        }
    });
}
}

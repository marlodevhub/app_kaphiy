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
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.ui.barista.ordenes.components.OrderCardBaristaAdapter;

import java.util.ArrayList;

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

        recyclerView = view.findViewById(R.id.recycle_ordenes_espera);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderCardBaristaAdapter();
        recyclerView.setAdapter(adapter);

        AppContainer container = ((MainApplication) requireActivity().getApplication()).appContainer;
        viewModel = new ViewModelProvider(requireActivity(),
                new BaristaOrdenesViewModelFactory(container.orderUseCases))
                .get(BaristaOrderViewModel.class);

        // Observa cambios de pedidos
        viewModel.getPendingOrders().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                adapter.submitList(new ArrayList<>(result.data)); // ✅ Nueva lista
            }
        });

        adapter.setOnItemClickListener(order -> viewModel.startPreparation(order.getId()));
    }



    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycle_ordenes_espera);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderCardBaristaAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(order -> viewModel.startPreparation(order.getId()));
    }

    private void setupViewModel() {
        AppContainer container = ((MainApplication) requireActivity().getApplication()).appContainer;

        // ⚡ Usamos requireActivity() para que el ViewModel sea compartido y reactivo
        viewModel = new ViewModelProvider(requireActivity(),
                new BaristaOrdenesViewModelFactory(container.orderUseCases))
                .get(BaristaOrderViewModel.class);
    }

    private void observePendingOrders() {
        viewModel.getPendingOrders().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                // ❌ Esto NO funciona si la lista se modifica internamente
                // adapter.submitList(result.data);

                // ✅ Creamos una nueva lista para forzar la actualización
                adapter.submitList(new ArrayList<>(result.data));
            }
        });
    }
}

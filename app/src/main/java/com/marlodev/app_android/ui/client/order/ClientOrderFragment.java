package com.marlodev.app_android.ui.client.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.marlodev.app_android.databinding.FragmentClientOrderBinding;
import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.repository.OrderRepository;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

import java.util.Collections;

public class ClientOrderFragment extends Fragment {

    private FragmentClientOrderBinding binding;
    private ClientOrderViewModel viewModel;

    private OrderAdapter activeOrdersAdapter;
    private OrderAdapter historyOrdersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClientOrderBinding.inflate(inflater, container, false);

        setupAdapters();
        setupViewModel();
        observeViewModel();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // prevenir memory leaks
    }

    // ----------------------------------------
    // Inicialización de Adapters
    // ----------------------------------------
    private void setupAdapters() {
        activeOrdersAdapter = new OrderAdapter();
        historyOrdersAdapter = new OrderAdapter();

        binding.activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.activeOrdersRecyclerView.setAdapter(activeOrdersAdapter);
        binding.activeOrdersRecyclerView.setNestedScrollingEnabled(false);

        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.historyRecyclerView.setAdapter(historyOrdersAdapter);
        binding.historyRecyclerView.setNestedScrollingEnabled(false);
    }

    // ----------------------------------------
    // Inicialización de ViewModel
    // ----------------------------------------
    private void setupViewModel() {
        OrderApi api = ApiClient.getClient(requireContext()).create(OrderApi.class);
        OrderRepository repository = new OrderRepository(api);

        ClientOrderViewModelFactory factory = new ClientOrderViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(ClientOrderViewModel.class);
    }

    // ----------------------------------------
    // Observadores de LiveData
    // ----------------------------------------
    private void observeViewModel() {

        // Órdenes activas
        viewModel.getActiveOrders().observe(getViewLifecycleOwner(), result -> {
//            handleOrderResult(result, activeOrdersAdapter, binding.activeOrdersProgress);
        });

        // Historial de órdenes
        viewModel.getHistoryOrders().observe(getViewLifecycleOwner(), result -> {
//            handleOrderResult(result, historyOrdersAdapter, binding.historyProgress);
        });

        // Errores genéricos
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // ----------------------------------------
    // Manejo de resultados de órdenes
    // ----------------------------------------
//    private void handleOrderResult(Result<java.util.List<Order>> result, OrderAdapter adapter, View progressBar) {
//        switch (result.getStatus()) {
//            case LOADING:
//                progressBar.setVisibility(View.VISIBLE);
//                adapter.submitList(Collections.emptyList());
//                break;
//            case SUCCESS:
//                progressBar.setVisibility(View.GONE);
//                adapter.submitList(result.getData());
//                break;
//            case ERROR:
//                progressBar.setVisibility(View.GONE);
//                adapter.submitList(Collections.emptyList());
//                Snackbar.make(binding.getRoot(), result.getMessage(), Snackbar.LENGTH_LONG).show();
//                break;
//        }
//    }
}

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
import com.marlodev.app_android.di.DependencyProvider;
import com.marlodev.app_android.ui.client.order.components.active_orders.ActiveOrderAdapter;
import com.marlodev.app_android.utils.Result;

public class ClientOrderFragment extends Fragment {
    private FragmentClientOrderBinding binding;
    private ClientOrderViewModel orderVM;
    private ActiveOrderAdapter activeOrdersAdapter;
    private ActiveOrderAdapter historyOrdersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClientOrderBinding.inflate(inflater, container, false);
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
        activeOrdersAdapter = new ActiveOrderAdapter();
        historyOrdersAdapter = new ActiveOrderAdapter();

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
        ClientOrderViewModelFactory factory = DependencyProvider.provideClientOrderViewModelFactory(requireContext());
        orderVM = new ViewModelProvider(this, factory).get(ClientOrderViewModel.class);
    }

    // ----------------------------------------
    // Observadores de LiveData
    // ----------------------------------------
    private void observeViewModel() {

        // Órdenes activas
        orderVM.getActiveOrders().observe(getViewLifecycleOwner(), result -> {
//            handleOrderResult(result, activeOrdersAdapter, binding.activeOrdersProgress);
        });

        // Historial de órdenes
        orderVM.getHistoryOrders().observe(getViewLifecycleOwner(), result -> {
//            handleOrderResult(result, historyOrdersAdapter, binding.historyProgress);
        });

        // Resultado del Checkout
        orderVM.getCheckoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Result.Status.SUCCESS) {
                Snackbar.make(binding.getRoot(), "Compra realizada con éxito", Snackbar.LENGTH_LONG).show();
            } else if (result.status == Result.Status.ERROR) {
                Snackbar.make(binding.getRoot(), "Error al realizar la compra: " + result.message, Snackbar.LENGTH_LONG).show();
            }
        });

        // Errores genéricos
        orderVM.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

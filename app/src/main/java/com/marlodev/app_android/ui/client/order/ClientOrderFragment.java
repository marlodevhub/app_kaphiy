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
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.repository.OrderRepository;

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
        View view = binding.getRoot();

        // Lógica de UI simplificada. Toda la complejidad de insets ha sido eliminada.

        initAdapters();
        initViewModel();
        setupObservers();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }

    /** Initializes the Adapters and LayoutManagers */
    private void initAdapters() {
        activeOrdersAdapter = new OrderAdapter();
        historyOrdersAdapter = new OrderAdapter();

        binding.activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.activeOrdersRecyclerView.setAdapter(activeOrdersAdapter);
        binding.activeOrdersRecyclerView.setNestedScrollingEnabled(false);

        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.historyRecyclerView.setAdapter(historyOrdersAdapter);
        binding.historyRecyclerView.setNestedScrollingEnabled(false);
    }

    /** Initializes the ViewModel with Repository */
    private void initViewModel() {
        OrderApi api = ApiClient.getClient(requireContext()).create(OrderApi.class);
        OrderRepository repository = new OrderRepository(api);
        ClientOrderViewModelFactory factory = new ClientOrderViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(ClientOrderViewModel.class);
    }

    /** Configures the ViewModel observers */
    private void setupObservers() {
        viewModel.getActiveOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                activeOrdersAdapter.submitList(orders);
            }
        });

        viewModel.getHistoryOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                historyOrdersAdapter.submitList(orders);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

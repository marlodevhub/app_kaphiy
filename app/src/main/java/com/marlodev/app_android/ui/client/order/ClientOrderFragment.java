package com.marlodev.app_android.ui.client.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.databinding.FragmentClientOrderBinding;
import com.marlodev.app_android.ui.client.order.components.active_orders.ActiveOrderAdapter;

public class ClientOrderFragment extends Fragment {
    private FragmentClientOrderBinding binding;
    private ClientOrderViewModel orderVM;
    private ActiveOrderAdapter activeOrdersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClientOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (orderVM != null) {
        }
    }

    private void  setupListeners(){}

    private void setupViewModel() {
        MainApplication app = (MainApplication) requireActivity().getApplicationContext();

        ClientOrderViewModelFactory factory = app.appContainer.clientOrderViewModelFactory;
        orderVM = new ViewModelProvider(requireActivity(), factory).get(ClientOrderViewModel.class);

        orderVM.orders.observe(getViewLifecycleOwner(), activeOrdersAdapter::submitList);

        orderVM.errorMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                showError(message);
            }
        });

    }


    private void setupRecyclerView() {
        activeOrdersAdapter = new ActiveOrderAdapter();
        binding.activeOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.activeOrdersRecyclerView.setAdapter(activeOrdersAdapter);
    }

    private void showError(String message) {
        if (message != null && !message.isBlank()) {
//            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

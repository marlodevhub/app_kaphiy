package com.marlodev.app_android.ui.client.cart;

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
import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.ui.client.cart.components.ItemProductCarAdapter;
import com.marlodev.app_android.utils.Result;

public class ClientCarFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartViewModel cartVM;
    private ItemProductCarAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartVM != null) {
            cartVM.refreshCart();
        }
    }

    private void setupListeners() {
        binding.btnRealizarOrder.setOnClickListener(v -> cartVM.checkout());
        cartAdapter.setOnQuantityChangeListener(cartVM::updateQuantity);
        cartAdapter.setOnDeleteClickListener(cartVM::deleteItem);
    }

    private void setupViewModel() {

        MainApplication app = (MainApplication) requireActivity().getApplicationContext();

        ClientCartViewModelFactory factory = app.appContainer.clientCartViewModelFactory;
        cartVM = new ViewModelProvider(requireActivity(), factory).get(ClientCartViewModel.class);


        binding.setViewModel(cartVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // --- Observadores de Estado Persistente ---
        cartVM.isCartEmpty.observe(getViewLifecycleOwner(), this::updateCartView);
        cartVM.cartItems.observe(getViewLifecycleOwner(), cartAdapter::submitList);
        cartVM.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null && binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        cartVM.errorMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                showError(message);
            }
        });

        cartVM.checkoutResult.observe(getViewLifecycleOwner(), event -> {
            Result<com.marlodev.app_android.domain.model.Order> result = event.getContentIfNotHandled();
            if (result != null) {
                if (result.status == Result.Status.SUCCESS) {
                    // Mostrar Toast o Snackbar
                    Toast.makeText(requireContext(), "Orden creada exitosamente", Toast.LENGTH_SHORT).show();
                    // Y navegar a otra pantalla
                    // NavHostFragment.findNavController(this).navigate(R.id.action_cartFragment_to_orderSuccessFragment);
                } else if (result.status == Result.Status.ERROR) {
                    showError(result.message);
                }
            }
        });

    }

    private void setupRecyclerView() {
        cartAdapter = new ItemProductCarAdapter();
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartItemsRecyclerView.setAdapter(cartAdapter);
    }

    private void updateCartView(boolean isEmpty) {
        if (binding == null) return;
        if (isEmpty) {
            binding.emptyCartView.setVisibility(View.VISIBLE);
            binding.cartContentContainer.setVisibility(View.GONE);
        } else {
            binding.emptyCartView.setVisibility(View.GONE);
            binding.cartContentContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        if (message != null && !message.isBlank()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

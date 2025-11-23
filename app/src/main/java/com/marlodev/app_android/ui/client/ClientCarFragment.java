package com.marlodev.app_android.ui.client;

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

import com.marlodev.app_android.adapter.client.ItemProductCarAdapter;
import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.dto.order.CartItem;
import com.marlodev.app_android.utils.CartNotifier;
import com.marlodev.app_android.viewmodel.ClientCartVM;

import java.util.List;

public class ClientCarFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartVM cartVM;
    private ItemProductCarAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Lógica de UI simplificada. Toda la complejidad de insets ha sido eliminada.

        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void setupViewModel() {
        cartVM = new ViewModelProvider(requireActivity()).get(ClientCartVM.class);

        cartVM.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);
        cartVM.getTotalItems().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalItems.setText(String.valueOf(total))
        );
        cartVM.getTotalPrice().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalPriceCar.setText("S/. " + total)
        );
        cartVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        // 🔹 Escuchar cambios globales del carrito
        CartNotifier.getCartUpdated().observe(getViewLifecycleOwner(), updated -> {
            if (updated != null && updated) cartVM.refreshCart();
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new ItemProductCarAdapter();
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartItemsRecyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnQuantityChangeListener(cartVM::updateQuantity);
        cartAdapter.setOnDeleteClickListener(cartVM::deleteItem);
    }

    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnRealizarOrder.setOnClickListener(v -> cartVM.checkout());
    }

    private void updateCartItems(List<CartItem> items) {
        cartAdapter.setItems(items);
        binding.cartItemsRecyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        if (message != null && !message.isBlank()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

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

import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.di.DependencyProvider;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.ui.client.cart.components.ItemProductCarAdapter;
import com.marlodev.app_android.utils.Result;

import java.util.List;

public class ClientCarFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartViewModel cartVM;
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

        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the cart every time the fragment becomes visible
        if (cartVM != null) {
            cartVM.refreshCart();
        }
    }

    private void setupListeners() {
        binding.btnRealizarOrder.setOnClickListener(v -> {
            cartVM.checkout();
        });
    }

    private void setupViewModel() {
        ClientCartViewModelFactory factory = DependencyProvider.provideClientCartViewModelFactory(requireContext());

        cartVM = new ViewModelProvider(requireActivity(), factory).get(ClientCartViewModel.class);

        cartVM.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);
        cartVM.getTotalItems().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalItems.setText(String.valueOf(total))
        );
        cartVM.getTotalPrice().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalPriceCar.setText("S/. " + total)
        );
        cartVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
        cartVM.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        cartVM.getCheckoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Result.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Orden creada exitosamente", Toast.LENGTH_SHORT).show();
                // Aquí puedes navegar a otra pantalla, por ejemplo, el historial de órdenes
            } else if (result.status == Result.Status.ERROR) {
                showError(result.message);
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new ItemProductCarAdapter();
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartItemsRecyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnQuantityChangeListener(cartVM::updateQuantity);
        cartAdapter.setOnDeleteClickListener(cartVM::deleteItem);
    }

    private void updateCartItems(List<CartItem> items) {
        cartAdapter.submitList(items);
        if (binding != null) {
            binding.cartItemsRecyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        }


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

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
import com.marlodev.app_android.ui.client.cart.components.ItemProductCarAdapter;
import com.marlodev.app_android.utils.Result;

public class ClientCarFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartViewModel cartVM;
    private ItemProductCarAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Orden de inicialización corregido para evitar NullPointerException
        setupRecyclerView(); // 1. Inicializa el Adapter
        setupViewModel();    // 2. Inicializa el ViewModel y usa el Adapter
        setupListeners();    // 3. Configura todos los listeners
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

        // Los listeners del adapter se configuran aquí, cuando tanto el adapter como el VM existen.
        cartAdapter.setOnQuantityChangeListener(cartVM::updateQuantity);
        cartAdapter.setOnDeleteClickListener(cartVM::deleteItem);
    }

    private void setupViewModel() {
        ClientCartViewModelFactory factory = DependencyProvider.provideClientCartViewModelFactory(requireContext());
        cartVM = new ViewModelProvider(requireActivity(), factory).get(ClientCartViewModel.class);

        // --- OBSERVADOR CLAVE PARA LA UI ---
        cartVM.getIsCartEmpty().observe(getViewLifecycleOwner(), this::updateCartView);

        // Observador para la lista de items
        cartVM.getCartItems().observe(getViewLifecycleOwner(), cartAdapter::submitList);

        // Observadores para los detalles (total, precio, etc.)
        cartVM.getTotalItems().observe(getViewLifecycleOwner(), total -> {
            if (binding != null) binding.txtTotalItems.setText(String.valueOf(total));
        });
        cartVM.getTotalPrice().observe(getViewLifecycleOwner(), total -> {
            if (binding != null) binding.txtTotalPriceCar.setText("S/. " + total);
        });
        cartVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
        cartVM.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null && binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        cartVM.getCheckoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Result.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Orden creada exitosamente", Toast.LENGTH_SHORT).show();
            } else if (result.status == Result.Status.ERROR) {
                showError(result.message);
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new ItemProductCarAdapter();
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartItemsRecyclerView.setAdapter(cartAdapter);
        // Los listeners se han movido a setupListeners() para evitar dependencias circulares.
    }

    /**
     * Controla la visibilidad de la pantalla principal vs. la pantalla de "Carrito Vacío".
     * @param isEmpty El estado que viene directamente del ViewModel.
     */
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

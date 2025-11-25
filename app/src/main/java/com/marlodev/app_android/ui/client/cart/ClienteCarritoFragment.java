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

import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.di.DependencyProvider;
import com.marlodev.app_android.utils.Result;

public class ClienteCarritoFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartViewModel viewModel;
    // private TuAdaptador cartAdapter; // Reemplaza con tu adaptador

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        observeViewModel();

        binding.btnRealizarOrder.setOnClickListener(v -> viewModel.checkout());
    }

    private void setupViewModel() {
        ClientCartViewModelFactory factory = DependencyProvider.provideClientCartViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, factory).get(ClientCartViewModel.class);
    }

    private void observeViewModel() {
        // OBSERVADOR PRINCIPAL Y ÚNICO para la visibilidad de la UI.
        // Reacciona al estado explícito que envía el ViewModel.
        viewModel.getIsCartEmpty().observe(getViewLifecycleOwner(), this::updateCartView);

        // Observador para actualizar la lista del RecyclerView
        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            // cartAdapter.submitList(cartItems);
        });

        // Observadores para detalles de la UI (precios, totales)
        viewModel.getTotalItems().observe(getViewLifecycleOwner(), total -> {
            binding.titleText.setText("Cesta(" + (total != null ? total : 0) + ")");
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), price -> {
             String formattedPrice = "S/. " + (price != null ? price.toString() : "0.00");
            binding.txtTotalPriceCar.setText(formattedPrice);
            binding.txtTotalItems.setText(formattedPrice);
        });

        // Observador para mostrar el resultado del checkout (Toast)
        viewModel.getCheckoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    /**
     * Controla la visibilidad de la pantalla del carrito vs. la pantalla de "Carrito Vacío".
     * @param isEmpty El estado inequívoco que proviene del ViewModel.
     */
    private void updateCartView(boolean isEmpty) {
        if (isEmpty) {
            binding.emptyCartView.setVisibility(View.VISIBLE);
            binding.cartContentContainer.setVisibility(View.GONE);
        } else {
            binding.emptyCartView.setVisibility(View.GONE);
            binding.cartContentContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

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
        viewModel.getCheckoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
                    // Aquí puedes navegar a otra pantalla, por ejemplo, la de historial de pedidos
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + result.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

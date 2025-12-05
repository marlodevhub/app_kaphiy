package com.marlodev.app_android.ui.delivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marlodev.app_android.databinding.FragmentDeliveryHistorialBinding;
import com.marlodev.app_android.domain.model.Pedido;
import com.marlodev.app_android.ui.delivery.chat.HistorialAdapter;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHistorialFragment extends Fragment {

    private FragmentDeliveryHistorialBinding binding;
    private DeliveryViewModel viewModel;
    private HistorialAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDeliveryHistorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DeliveryViewModel.class);

        adapter = new HistorialAdapter();
        binding.recyclerHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerHistorial.setAdapter(adapter);

        observeHistorial();
    }

    private void observeHistorial() {
        viewModel.pedidos.observe(getViewLifecycleOwner(), pedidos -> {

            List<Pedido> filtrados = new ArrayList<>();

            for (Pedido p : pedidos) {
                if (p.isAceptado() && p.isEntregado()) {
                    filtrados.add(p);
                }
            }

            adapter.submitList(filtrados);
        });
    }
}


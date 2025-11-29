package com.marlodev.app_android.ui.delivery;

import android.content.Context;
import android.content.Intent;
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

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.ui.client.order.PedidoAdapter;
import com.marlodev.app_android.databinding.FragmentDeliveryHomeBinding;
import com.marlodev.app_android.domain.model.Pedido;
import com.marlodev.app_android.utils.SessionManager;

public class DeliveryHomeFragment extends Fragment implements PedidoAdapter.OnPedidoClickListener {

    private FragmentDeliveryHomeBinding binding;
    private DeliveryViewModel viewModel;
    private PedidoAdapter pedidoAdapter;
    private OnPedidoSelectedListener listener;

    public interface OnPedidoSelectedListener {
        void onPedidoSelected(Pedido pedido);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoSelectedListener) {
            listener = (OnPedidoSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnPedidoSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeliveryHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        setupRecyclerView();
        setupLogoutButton();
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevenir fugas de memoria
    }

    private void setupRecyclerView() {
        pedidoAdapter = new PedidoAdapter(this);
    }

    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clear();
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void observeViewModel() {
        viewModel.pedidos.observe(getViewLifecycleOwner(), pedidos -> {
            // El Fragment solo se encarga de pasar la lista al adapter.
            // El adapter y el ViewModel se encargan de la lógica del esqueleto.
            pedidoAdapter.submitList(pedidos);
        });
    }

    // --- Implementación de los clicks del adapter ---

    @Override
    public void onAceptarClick(Pedido pedido) {
    }

    @Override
    public void onDetalleClick(Pedido pedido) {
        if (listener != null) {
            listener.onPedidoSelected(pedido);
        }
    }
}

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

import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.data.network.model.order.CartItem;
import com.marlodev.app_android.utils.CartNotifier;

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

        return view;
    }

    private void setupViewModel() {
        CartApi cartApi = ApiClient.getClient(requireContext()).create(CartApi.class);
        CartRepository cartRepository = new CartRepository(cartApi);
        ClientCartViewModelFactory factory = new ClientCartViewModelFactory(cartRepository);

        cartVM = new ViewModelProvider(requireActivity(), factory).get(ClientCartViewModel.class);

        cartVM.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);
        cartVM.getTotalItems().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalItems.setText(String.valueOf(total))
        );
        cartVM.getTotalPrice().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalPriceCar.setText("S/. " + total)
        );
        cartVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

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

    private void updateCartItems(List<CartItem> items) {
        cartAdapter.submitList(items);
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

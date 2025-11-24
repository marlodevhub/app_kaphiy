package com.marlodev.app_android.ui.client.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.marlodev.app_android.data.repository.CartRepository;

public class ClientCartViewModelFactory implements ViewModelProvider.Factory {

    private final CartRepository cartRepository;

    public ClientCartViewModelFactory(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientCartViewModel.class)) {
            return (T) new ClientCartViewModel(cartRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

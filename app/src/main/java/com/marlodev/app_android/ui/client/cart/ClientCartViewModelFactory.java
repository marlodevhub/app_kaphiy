package com.marlodev.app_android.ui.client.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.domain.usecase.cart.CartUseCases;

public class ClientCartViewModelFactory implements ViewModelProvider.Factory {

    private final CartUseCases cartUseCases;

    public ClientCartViewModelFactory(CartUseCases cartUseCases) {
        this.cartUseCases = cartUseCases;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientCartViewModel.class)) {
            return (T) new ClientCartViewModel(cartUseCases);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

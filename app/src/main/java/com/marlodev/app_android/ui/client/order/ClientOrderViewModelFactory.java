package com.marlodev.app_android.ui.client.order;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.data.repository.OrderRepository;
import com.marlodev.app_android.domain.usecase.cart.CheckoutUseCase;

public class ClientOrderViewModelFactory implements ViewModelProvider.Factory {

    private final OrderRepository repository;
    private final CheckoutUseCase checkoutUseCase;

    public ClientOrderViewModelFactory(OrderRepository repository, CheckoutUseCase checkoutUseCase) {
        this.repository = repository;
        this.checkoutUseCase = checkoutUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientOrderViewModel.class)) {
            return (T) new ClientOrderViewModel(repository, checkoutUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

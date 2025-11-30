package com.marlodev.app_android.ui.client.order;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.domain.usecase.order.OrderUseCases;

public class ClientOrderViewModelFactory implements ViewModelProvider.Factory {

    private final OrderUseCases orderUseCases;

    public ClientOrderViewModelFactory(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientOrderViewModel.class)) {
            return (T) new ClientOrderViewModel(orderUseCases);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

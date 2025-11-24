package com.marlodev.app_android.ui.client.order;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.data.repository.OrderRepository;

public class ClientOrderViewModelFactory implements ViewModelProvider.Factory {

    private final OrderRepository repository;

    public ClientOrderViewModelFactory(OrderRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientOrderViewModel.class)) {
            return (T) new ClientOrderViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

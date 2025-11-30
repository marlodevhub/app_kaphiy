package com.marlodev.app_android.ui.barista.ordenes;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.domain.usecase.order.cliente.OrderUseCases;

public class BaristaOrdenesViewModelFactory implements ViewModelProvider.Factory{
    private final OrderUseCases orderUseCases;

    public BaristaOrdenesViewModelFactory(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BaristaOrderViewModel.class)) {
//            return (T) new BaristaOrderViewModel(orderUseCases);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

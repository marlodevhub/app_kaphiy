package com.marlodev.app_android.ui.barista.mas;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.domain.usecase.order.OrderUseCases;

public class BaristaOrderDetailViewModelFactory implements ViewModelProvider.Factory {

    private final OrderUseCases orderUseCases;

    public BaristaOrderDetailViewModelFactory(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BaristaOrderDetailViewModel.class)) {
            return (T) new BaristaOrderDetailViewModel(orderUseCases);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

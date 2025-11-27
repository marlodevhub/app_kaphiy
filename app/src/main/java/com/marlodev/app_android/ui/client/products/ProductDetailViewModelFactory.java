package com.marlodev.app_android.ui.client.products;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.product.GetProductByIdUseCase;
import com.marlodev.app_android.domain.usecase.product.ProductUseCases;

public class ProductDetailViewModelFactory implements ViewModelProvider.Factory {

    private final ProductUseCases productUseCases;

    public ProductDetailViewModelFactory(ProductUseCases productUseCases) {
        this.productUseCases = productUseCases;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductDetailViewModel.class)) {
            return (T) new ProductDetailViewModel(productUseCases);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

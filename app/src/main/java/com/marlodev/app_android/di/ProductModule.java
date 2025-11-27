package com.marlodev.app_android.di;

import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.domain.usecase.product.GetProductByIdUseCase;
import com.marlodev.app_android.domain.usecase.product.ProductUseCases;

public class ProductModule {
    public static ProductUseCases provideProductUseCases(ProductRepositoryImpl repository) {
        GetProductByIdUseCase getProductById = new GetProductByIdUseCase(repository);
        return new ProductUseCases(getProductById);
    }
}

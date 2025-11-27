package com.marlodev.app_android.domain.usecase.product;

import com.marlodev.app_android.domain.usecase.order.cliente.GetActiveOrdersUseCase;

public class ProductUseCases {

    private final GetProductByIdUseCase getProductById;

    public ProductUseCases(GetProductByIdUseCase  getProductByIdUseCase)
    {
        this.getProductById  = getProductByIdUseCase ;
    }
    public GetProductByIdUseCase getProductByIdUseCase() {
        return getProductById;
    }
}

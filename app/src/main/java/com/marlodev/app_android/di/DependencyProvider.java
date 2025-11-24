package com.marlodev.app_android.di;

import android.content.Context;

import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.api.ProductApiService;
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.domain.usecase.product.GetProductByIdUseCase;
import com.marlodev.app_android.ui.client.cart.ClientCartViewModelFactory;
import com.marlodev.app_android.ui.client.products.ProductDetailViewModelFactory;

public class DependencyProvider {

    private static ProductApiService productApiService;
    private static ProductRepositoryImpl productRepository;
    private static GetProductByIdUseCase getProductByIdUseCase;

    private static CartApi cartApi;
    private static CartRepository cartRepository;
    private static CartUseCases cartUseCases;

    public static ProductRepositoryImpl provideProductRepository(Context context) {
        if (productRepository == null) {
            productRepository = new ProductRepositoryImpl(provideProductApiService(context.getApplicationContext()));
        }
        return productRepository;
    }

    private static ProductApiService provideProductApiService(Context context) {
        if (productApiService == null) {
            productApiService = ApiClient.getClient(context.getApplicationContext()).create(ProductApiService.class);
        }
        return productApiService;
    }

    public static GetProductByIdUseCase provideGetProductByIdUseCase(Context context) {
        if (getProductByIdUseCase == null) {
            getProductByIdUseCase = new GetProductByIdUseCase(provideProductRepository(context.getApplicationContext()));
        }
        return getProductByIdUseCase;
    }

    public static ProductDetailViewModelFactory provideProductDetailViewModelFactory(Context context) {
        return new ProductDetailViewModelFactory(provideGetProductByIdUseCase(context.getApplicationContext()));
    }

    public static CartRepository provideCartRepository(Context context) {
        if (cartRepository == null) {
            cartRepository = new CartRepository(provideCartApi(context.getApplicationContext()));
        }
        return cartRepository;
    }

    private static CartApi provideCartApi(Context context) {
        if (cartApi == null) {
            cartApi = ApiClient.getClient(context.getApplicationContext()).create(CartApi.class);
        }
        return cartApi;
    }

    public static CartUseCases provideCartUseCases(Context context) {
        if (cartUseCases == null) {
            cartUseCases = CartModule.provideCartUseCases(provideCartRepository(context));
        }
        return cartUseCases;
    }

    public static ClientCartViewModelFactory provideClientCartViewModelFactory(Context context) {
        return new ClientCartViewModelFactory(provideCartUseCases(context.getApplicationContext()));
    }
}

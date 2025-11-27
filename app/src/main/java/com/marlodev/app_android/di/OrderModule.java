package com.marlodev.app_android.di;

import com.marlodev.app_android.data.repository.OrderRepositoryImpl;
import com.marlodev.app_android.domain.usecase.order.cliente.GetActiveOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.cliente.OrderUseCases;

public class OrderModule {
    public static OrderUseCases provideCartUseCases(OrderRepositoryImpl repository) {
        GetActiveOrdersUseCase getActiveOrders = new GetActiveOrdersUseCase(repository);
        return new OrderUseCases(getActiveOrders);
    }
}

package com.marlodev.app_android.domain.usecase.order.cliente;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.OrderRepositoryImpl;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

import java.util.List;

// Cliente Obtener actives de órdenes
public class GetActiveOrdersUseCase {

    private final OrderRepositoryImpl repository;

    public GetActiveOrdersUseCase(OrderRepositoryImpl repository) {
        this.repository = repository;
    }

    public LiveData<Result<List<Order>>>execute() {
        return repository.getActiveOrders();
    }
}

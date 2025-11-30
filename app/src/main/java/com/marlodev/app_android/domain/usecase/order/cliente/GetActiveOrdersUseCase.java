package com.marlodev.app_android.domain.usecase.order.cliente;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Caso de uso para obtener las órdenes activas del cliente.
 */
public class GetActiveOrdersUseCase {

    private final OrderRepository repository;

    public GetActiveOrdersUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<List<Order>>> execute() {
        return repository.getActiveOrders();
    }
}
package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Caso de uso para que un barista obtenga las órdenes pendientes (EN_ESPERA).
 */
public class GetPendingOrdersUseCase {

    private final OrderRepository repository;

    public GetPendingOrdersUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<List<Order>>> execute() {
        return repository.getQueueBarista();
    }
}
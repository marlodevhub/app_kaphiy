package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Mis ordenes (Falta verificar implemnetacion)
 */
public class GetBaristaOrderHistoryUseCase {

    private final OrderRepository repository;

    public GetBaristaOrderHistoryUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<List<Order>>> execute() {
        return repository.getMyOrdersBarista();
    }
}

package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 *  Marcar orden como listo
 */
public class SetOrderReadyUseCase {

    private final OrderRepository repository;

    public SetOrderReadyUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<Order>> execute(long orderId) {
        return repository.markReadyBarista(orderId);
    }
}

package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * marcándolo como EN_PREPARACION. //✅
 */
public class AcceptOrderUseCase {
    private final OrderRepository repository;

    public AcceptOrderUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<Order>> execute(long orderId) {
        return repository.startPreparationBarista(orderId);
    }
}

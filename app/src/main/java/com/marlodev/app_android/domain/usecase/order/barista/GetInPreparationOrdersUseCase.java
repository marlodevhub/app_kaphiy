package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

  // Lista de órdenes en preparación ✅
public class GetInPreparationOrdersUseCase {

    private final OrderRepository repository;

    public GetInPreparationOrdersUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<List<Order>>> execute() {
        return repository.getInPreparationBarista();
    }
}

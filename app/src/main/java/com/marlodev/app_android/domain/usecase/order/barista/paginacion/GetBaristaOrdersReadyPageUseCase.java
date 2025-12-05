package com.marlodev.app_android.domain.usecase.order.barista.paginacion;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

public class GetBaristaOrdersReadyPageUseCase {
    private final OrderRepository repository;

    public GetBaristaOrdersReadyPageUseCase(OrderRepository repository) {
        this.repository = repository;
    }
    public LiveData<Result<PageResponse<Order>>> execute(int page, int size) {
        return repository.getBaristaOrdersReadyPage(page, size);
    }
}

package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

public class GetinPreparationOrdersInPreparationUseCase {
    private final OrderRepository repository;

    public GetinPreparationOrdersInPreparationUseCase(OrderRepository repository) {
        this.repository = repository;
    }


    public LiveData<Result<PageResponse<Order>>> execute(int page, int size) {
        return repository.getBaristaOrdersInPreparationPage(page, size);
    }
}

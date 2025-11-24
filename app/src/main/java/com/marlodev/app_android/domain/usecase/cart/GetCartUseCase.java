package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

public class GetCartUseCase {

    private final CartRepository repository;

    public GetCartUseCase(CartRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<Order>> execute() {
        return repository.getCart();
    }
}

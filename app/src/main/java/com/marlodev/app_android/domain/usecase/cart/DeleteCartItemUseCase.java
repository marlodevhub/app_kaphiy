package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

public class DeleteCartItemUseCase {

    private final CartRepository repository;

    public DeleteCartItemUseCase(CartRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<Order>> execute(Long itemId) {
        return repository.deleteItem(itemId);
    }
}

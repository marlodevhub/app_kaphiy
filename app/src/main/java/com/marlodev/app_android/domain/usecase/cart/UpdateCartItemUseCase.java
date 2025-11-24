package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

public class UpdateCartItemUseCase {

    private final CartRepository repository;

    public UpdateCartItemUseCase(CartRepository repository) {
        this.repository = repository;
    }

    public LiveData<Result<Order>> execute(Long itemId, CartItem item) {
        return repository.updateItem(itemId, item);
    }
}

package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.CartRepository;
import com.marlodev.app_android.utils.Result;

public class CheckoutUseCase {

    private final CartRepository cartRepository;

    public CheckoutUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public LiveData<Result<Order>> execute() {
        return cartRepository.checkout();
    }
}

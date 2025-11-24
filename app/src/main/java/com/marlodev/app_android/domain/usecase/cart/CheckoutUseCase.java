package com.marlodev.app_android.domain.usecase.cart;

import com.marlodev.app_android.domain.DomainCallback;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.CartRepository;

public class CheckoutUseCase {

    private final CartRepository cartRepository;

    public CheckoutUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void execute(DomainCallback<Order> callback) {
        cartRepository.checkout(callback);
    }
}

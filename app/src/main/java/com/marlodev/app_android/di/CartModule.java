package com.marlodev.app_android.di;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.usecase.cart.AddItemToCartUseCase;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.domain.usecase.cart.DeleteCartItemUseCase;
import com.marlodev.app_android.domain.usecase.cart.GetCartUseCase;
import com.marlodev.app_android.domain.usecase.cart.UpdateCartItemUseCase;

public class CartModule {

    public static CartUseCases provideCartUseCases(CartRepository repository) {
        return new CartUseCases(
                new AddItemToCartUseCase(repository),
                new GetCartUseCase(repository),
                new UpdateCartItemUseCase(repository),
                new DeleteCartItemUseCase(repository)
        );
    }
}

package com.marlodev.app_android.di;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.usecase.cart.AddItemToCartUseCase;
import com.marlodev.app_android.domain.usecase.cart.AddOrUpdateCartItemUseCase;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.domain.usecase.cart.DeleteCartItemUseCase;
import com.marlodev.app_android.domain.usecase.cart.GetCartUseCase;
import com.marlodev.app_android.domain.usecase.cart.UpdateCartItemUseCase;

public class CartModule {

    public static CartUseCases provideCartUseCases(CartRepository repository) {
        // Casos de uso atómicos
        AddItemToCartUseCase addItem = new AddItemToCartUseCase(repository);
        UpdateCartItemUseCase updateItem = new UpdateCartItemUseCase(repository);

        // Caso de uso inteligente que orquesta a los atómicos
        AddOrUpdateCartItemUseCase addOrUpdateItem = new AddOrUpdateCartItemUseCase(addItem, updateItem);

        // Construcción del contenedor final, ahora con UpdateCartItemUseCase restaurado
        return new CartUseCases(
                addOrUpdateItem,
                new GetCartUseCase(repository),
                updateItem, // <--- Proporcionado de nuevo
                new DeleteCartItemUseCase(repository)
        );
    }
}

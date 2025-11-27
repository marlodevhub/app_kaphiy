package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.repository.CartRepositoryImpl;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Caso de uso atómico para actualizar un artículo que ya existe en el carrito.
 * Su única responsabilidad es delegar la llamada de actualización al repositorio.
 * Es utilizado tanto directamente (para cambiar la cantidad desde el carrito) como internamente
 * por el caso de uso orquestador AddOrUpdateCartItemUseCase.
 */
public class UpdateCartItemUseCase {

    private final CartRepositoryImpl repository;

    public UpdateCartItemUseCase(CartRepositoryImpl repository) {
        this.repository = repository;
    }

    public LiveData<Result<Order>> execute(List<CartItem> currentItems, Long itemId, int newQty) {
        MutableLiveData<Result<Order>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Result.loading());

        // 1️⃣ Lógica optimista: crear nuevo CartItem
        CartItem itemToUpdate = null;
        List<CartItem> newItems = new ArrayList<>();
        for (CartItem item : currentItems) {
            if (Objects.equals(item.getId(), itemId)) {
                itemToUpdate = CartItem.builder()
                        .id(item.getId())
                        .product(item.getProduct())
                        .variant(item.getVariant())
                        .extras(item.getExtras())
                        .quantity(newQty)
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getUnitPrice() != null
                                ? item.getUnitPrice().multiply(BigDecimal.valueOf(newQty))
                                : null)
                        .build();
                newItems.add(itemToUpdate);
            } else {
                newItems.add(item);
            }
        }

        if (itemToUpdate == null) {
            resultLiveData.setValue(Result.error("Item no encontrado"));
            return resultLiveData;
        }

        // 2️⃣ Actualizar en repositorio
        repository.updateItem(itemId, itemToUpdate).observeForever(repoResult -> {
            if (repoResult.status != Result.Status.LOADING) {
                // Retornar resultado final
                resultLiveData.setValue(repoResult);
            }
        });

        return resultLiveData;
    }
}

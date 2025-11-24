package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso atómico para añadir un nuevo artículo al carrito.
 * Su única responsabilidad es delegar la creación del item al repositorio.
 * Este caso de uso es utilizado principalmente de forma interna por AddOrUpdateCartItemUseCase.
 */
public class AddItemToCartUseCase {

    private final CartRepository repository;

    /**
     * Constructor que inyecta el repositorio del carrito.
     * @param repository La implementación del repositorio que se comunicará con la fuente de datos.
     */
    public AddItemToCartUseCase(CartRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de añadir un nuevo artículo.
     *
     * @param item El nuevo CartItem a añadir al carrito.
     * @return Un LiveData que emite el resultado de la operación con la orden actualizada.
     */
    public LiveData<Result<Order>> execute(CartItem item) {
        return repository.addItem(item);
    }
}

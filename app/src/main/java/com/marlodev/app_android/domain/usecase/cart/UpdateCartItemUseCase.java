package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso atómico para actualizar un artículo que ya existe en el carrito.
 * Su única responsabilidad es delegar la llamada de actualización al repositorio.
 * Es utilizado tanto directamente (para cambiar la cantidad desde el carrito) como internamente
 * por el caso de uso orquestador AddOrUpdateCartItemUseCase.
 */
public class UpdateCartItemUseCase {

    private final CartRepository repository;

    /**
     * Constructor que inyecta el repositorio del carrito.
     * @param repository La implementación del repositorio que se comunicará con la fuente de datos.
     */
    public UpdateCartItemUseCase(CartRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de actualizar un artículo existente.
     *
     * @param itemId El ID del CartItem que se va a actualizar.
     * @param item El objeto CartItem con los datos actualizados.
     * @return Un LiveData que emite el resultado de la operación con la orden actualizada.
     */
    public LiveData<Result<Order>> execute(Long itemId, CartItem item) {
        return repository.updateItem(itemId, item);
    }
}

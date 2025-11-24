package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso atómico para eliminar un artículo específico del carrito de compras.
 * Su única responsabilidad es delegar la llamada de eliminación al repositorio.
 */
public class DeleteCartItemUseCase {

    private final CartRepository repository;

    /**
     * Constructor que inyecta el repositorio del carrito.
     * @param repository La implementación del repositorio que se comunicará con la fuente de datos.
     */
    public DeleteCartItemUseCase(CartRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de eliminar el artículo.
     *
     * @param itemId El ID del CartItem que se va a eliminar.
     * @return Un LiveData que emite el resultado de la operación con la orden actualizada.
     */
    public LiveData<Result<Order>> execute(Long itemId) {
        // Simplemente delega la llamada al método correspondiente del repositorio.
        return repository.deleteItem(itemId);
    }
}

package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso atómico para obtener el contenido completo del carrito de compras.
 * Su única responsabilidad es delegar la llamada de obtención al repositorio.
 */
public class GetCartUseCase {

    private final CartRepository repository;

    /**
     * Constructor que inyecta el repositorio del carrito.
     * @param repository La implementación del repositorio que se comunicará con la fuente de datos.
     */
    public GetCartUseCase(CartRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de obtener el carrito.
     *
     * @return Un LiveData que emite el resultado de la operación con la orden actualizada.
     */
    public LiveData<Result<Order>> execute() {
        return repository.getCart();
    }
}

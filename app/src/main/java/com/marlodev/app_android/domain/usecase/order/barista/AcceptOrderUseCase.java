package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso para que un barista acepte un pedido, marcándolo como EN_PREPARACION.
 */
public class AcceptOrderUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public AcceptOrderUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de aceptar un pedido.
     *
     * @param orderId ID del pedido que se va a aceptar.
     * @return LiveData con el resultado de la operación (orden actualizada).
     */
    public LiveData<Result<Order>> execute(long orderId) {
        return repository.startPreparationBarista(orderId);
    }
}

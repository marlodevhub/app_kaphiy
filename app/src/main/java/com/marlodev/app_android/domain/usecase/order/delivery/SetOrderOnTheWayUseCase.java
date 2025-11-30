package com.marlodev.app_android.domain.usecase.order.delivery;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso para que un delivery marque una orden como "EN_CAMINO".
 */
public class SetOrderOnTheWayUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public SetOrderOnTheWayUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de marcar una orden como en camino.
     *
     * @param orderId El ID de la orden que se desea marcar como en camino.
     * @return LiveData con el resultado de la operación (orden actualizada).
     */
    public LiveData<Result<Order>> execute(long orderId) {
        return repository.pickupOrderDelivery(orderId);
    }
}

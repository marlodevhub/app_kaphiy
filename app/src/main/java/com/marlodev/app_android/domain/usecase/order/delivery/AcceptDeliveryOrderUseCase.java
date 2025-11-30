package com.marlodev.app_android.domain.usecase.order.delivery;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso para que un delivery acepte un pedido y lo marque como EN_CAMINO.
 */
public class AcceptDeliveryOrderUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public AcceptDeliveryOrderUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de aceptar un pedido para entrega.
     *
     * @param orderId ID del pedido que se va a recoger.
     * @return LiveData con el resultado de la operación (orden actualizada).
     */
    public LiveData<Result<Order>> execute(long orderId) {
        return repository.pickupOrderDelivery(orderId);
    }
}

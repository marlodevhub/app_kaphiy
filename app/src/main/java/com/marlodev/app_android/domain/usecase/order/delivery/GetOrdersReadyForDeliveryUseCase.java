package com.marlodev.app_android.domain.usecase.order.delivery;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Caso de uso para que un delivery obtenga las órdenes que ya están listas para entregar.
 */
public class GetOrdersReadyForDeliveryUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public GetOrdersReadyForDeliveryUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de obtener las órdenes listas para entregar.
     *
     * @return LiveData con el resultado de la operación (lista de órdenes listas para entrega).
     */
    public LiveData<Result<List<Order>>> execute() {
        return repository.getReadyOrdersDelivery();
    }
}

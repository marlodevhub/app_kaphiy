package com.marlodev.app_android.domain.usecase.order.delivery;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Caso de uso para que un delivery obtenga el historial de órdenes entregadas o canceladas.
 */
public class GetDeliveryHistoryUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public GetDeliveryHistoryUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de obtener el historial de órdenes del delivery.
     *
     * @return LiveData con el resultado de la operación (lista de órdenes entregadas o canceladas).
     */
    public LiveData<Result<List<Order>>> execute() {
        return repository.getHistoryOrdersDelivery();
    }
}

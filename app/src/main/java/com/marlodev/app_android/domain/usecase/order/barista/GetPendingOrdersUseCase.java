package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Caso de uso para que un barista obtenga las órdenes pendientes (EN_ESPERA).
 */
public class GetPendingOrdersUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public GetPendingOrdersUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de obtener las órdenes pendientes.
     *
     * @return LiveData con el resultado de la operación (lista de órdenes).
     */
    public LiveData<Result<List<Order>>> execute() {
        return repository.getQueueBarista();
    }
}
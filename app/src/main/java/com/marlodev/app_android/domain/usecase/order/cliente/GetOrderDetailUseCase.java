package com.marlodev.app_android.domain.usecase.order.cliente;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso para que un cliente obtenga los detalles de una orden específica.
 */
public class GetOrderDetailUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public GetOrderDetailUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de obtener los detalles de una orden.
     *
     * @param orderId El ID de la orden que se desea consultar.
     * @return LiveData con el resultado de la operación (detalles de la orden).
     */
    public LiveData<Result<Order>> execute(long orderId) {
        return repository.getOrderById(orderId);
    }
}
package com.marlodev.app_android.domain.usecase.order.cliente;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

//  Caso de uso para obtener el historial
public class GetHistoryOrdersUseCase {

    private final OrderRepository repository;

    /**
     * Constructor que inyecta la interfaz del repositorio de órdenes.
     *
     * @param repository El repositorio de órdenes (interfaz).
     */
    public GetHistoryOrdersUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la acción de obtener todas las órdenes del cliente.
     *
     * @return LiveData con el resultado de la operación (lista de órdenes del cliente).
     */
    public LiveData<Result<List<Order>>> execute() {
        return repository.getOrderHistory(); // O repository.getOrderHistory() si quieres historial
    }
}

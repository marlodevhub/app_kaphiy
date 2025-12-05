package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Listar todas las órdenes que ya están listas para entrega.
 */
public class GetReadyOrdersBaristaUseCase {

    private final OrderRepository repository;

    public GetReadyOrdersBaristaUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la obtención de todas las órdenes listas para entrega.
     *
     * @return LiveData que emite el resultado con la lista de órdenes
     */
    public LiveData<Result<List<Order>>> execute() {
        return repository.getReadyOrdersBarista();
    }
}

package com.marlodev.app_android.domain.usecase.order.barista;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;

/**
 * Caso de uso para obtener todas las órdenes que están en preparación por el barista autenticado.
 */
public class GetInPreparationOrdersUseCase {

    private final OrderRepository repository;

    public GetInPreparationOrdersUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la obtención de todas las órdenes en preparación.
     *
     * @return LiveData que emite el resultado con la lista de órdenes
     */
    public LiveData<Result<List<Order>>> execute() {
        return repository.getInPreparationBarista();
    }
}

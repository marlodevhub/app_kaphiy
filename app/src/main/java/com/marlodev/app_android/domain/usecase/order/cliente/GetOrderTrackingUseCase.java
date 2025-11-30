package com.marlodev.app_android.domain.usecase.order.cliente;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso para obtener el tracking de una orden específica.
 */
public class GetOrderTrackingUseCase {

    private final OrderRepository repository;

    public GetOrderTrackingUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la obtención del tracking de la orden con el ID proporcionado.
     *
     * @param orderId ID de la orden
     * @return LiveData que emite el resultado con la información de tracking
     */
    public LiveData<Result<OrderTracking>> execute(long orderId) {
        return repository.getOrderTracking(orderId);
    }
}

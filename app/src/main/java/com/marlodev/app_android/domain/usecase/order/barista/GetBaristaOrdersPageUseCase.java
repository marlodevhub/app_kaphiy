package com.marlodev.app_android.domain.usecase.order.barista;
import androidx.lifecycle.LiveData;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

/**
 * Caso de uso para obtener órdenes del barista por páginas (paginación).
 */
public class GetBaristaOrdersPageUseCase {

    private final OrderRepository repository;

    public GetBaristaOrdersPageUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta la obtención de una página de órdenes del barista.
     *
     * @param page número de página (0-indexed)
     * @param size tamaño de página
     * @return LiveData que emite el resultado con la página de órdenes
     */
    public LiveData<Result<PageResponse<Order>>> execute(int page, int size) {
        return repository.getBaristaOrdersPage(page, size);
    }
}
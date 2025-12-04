package com.marlodev.app_android.di;

import com.marlodev.app_android.data.repository.OrderRepositoryImpl;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.domain.usecase.order.barista.AcceptOrderUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetBaristaOrderHistoryUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetBaristaOrdersPageUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetInPreparationOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetPendingOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetReadyOrdersBaristaUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetReadyOrdersPageUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetinPreparationOrdersInPreparationUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.SetOrderReadyUseCase;
import com.marlodev.app_android.domain.usecase.order.cliente.GetActiveOrdersUseCase;

/**
 * OrderModule: Ensambla casos de uso por contexto (Cliente / Barista)
 * Eliminando redundancia y asegurando escalabilidad.
 */
public class OrderModule {

    public static OrderUseCases provideOrderUseCases(OrderRepositoryImpl repository) {

        // -------- CLIENTE --------
        OrderUseCases.Cliente cliente = new OrderUseCases.Cliente(
                new GetActiveOrdersUseCase(repository)
        );

        // -------- BARISTA --------
        OrderUseCases.Barista barista = new OrderUseCases.Barista(
                new AcceptOrderUseCase(repository),
                new GetBaristaOrdersPageUseCase(repository),
                new GetBaristaOrderHistoryUseCase(repository),
                new GetInPreparationOrdersUseCase(repository),
                new GetPendingOrdersUseCase(repository),
                new GetReadyOrdersBaristaUseCase(repository),
                new SetOrderReadyUseCase(repository),
                new GetinPreparationOrdersInPreparationUseCase(repository),
                new GetReadyOrdersPageUseCase(repository)
        );

        // -------- RETORNAR AGRUPADO --------
        return new OrderUseCases(cliente, barista);
    }
}

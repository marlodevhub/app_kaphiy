package com.marlodev.app_android.di;

import com.marlodev.app_android.data.repository.OrderRepositoryImpl;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.domain.usecase.order.barista.AcceptOrderUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetBaristaOrderHistoryUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetBaristaOrdersPageUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetInPreparationOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetPendingOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetReadyOrdersBaristaUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.SetOrderReadyUseCase;
import com.marlodev.app_android.domain.usecase.order.cliente.GetActiveOrdersUseCase;

public class OrderModule {

    public static OrderUseCases provideOrderUseCases(OrderRepositoryImpl repository) {

        // CASOS DE USO DE CLIENTE
        GetActiveOrdersUseCase getActiveOrders = new GetActiveOrdersUseCase(repository);

        // CASOS DE USO DE BARISTA
        AcceptOrderUseCase acceptOrder = new AcceptOrderUseCase(repository);
        GetBaristaOrderHistoryUseCase getBaristaHistory = new GetBaristaOrderHistoryUseCase(repository);
        GetInPreparationOrdersUseCase getInPreparationOrders = new GetInPreparationOrdersUseCase(repository);
        GetPendingOrdersUseCase getPendingOrders = new GetPendingOrdersUseCase(repository);
        GetReadyOrdersBaristaUseCase getReadyOrdersBarista = new GetReadyOrdersBaristaUseCase(repository);
        SetOrderReadyUseCase setOrderReady = new SetOrderReadyUseCase(repository);
        GetBaristaOrdersPageUseCase getBaristaOrdersPage = new GetBaristaOrdersPageUseCase(repository);


        return new OrderUseCases(
                getActiveOrders,
                acceptOrder,
                getBaristaHistory,
                getInPreparationOrders,
                getPendingOrders,
                getReadyOrdersBarista,
                setOrderReady,
                getBaristaOrdersPage
        );
    }
}

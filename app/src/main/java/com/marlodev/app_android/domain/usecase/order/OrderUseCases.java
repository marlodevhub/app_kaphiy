package com.marlodev.app_android.domain.usecase.order;

import com.marlodev.app_android.domain.usecase.order.barista.AcceptOrderUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetBaristaOrderHistoryUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetInPreparationOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetPendingOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetReadyOrdersBaristaUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.SetOrderReadyUseCase;
import com.marlodev.app_android.domain.usecase.order.cliente.GetActiveOrdersUseCase;

public class OrderUseCases {

    // CASOS DE USO DE CLIENTE
    private final GetActiveOrdersUseCase getActiveOrders;

    // CASOS DE USO DE BARISTA
    private final AcceptOrderUseCase acceptOrder;
    private final GetBaristaOrderHistoryUseCase getBaristaHistory;
    private final GetInPreparationOrdersUseCase getInPreparationOrders;
    private final GetPendingOrdersUseCase getPendingOrders;
    private final GetReadyOrdersBaristaUseCase getReadyOrdersBarista;
    private final SetOrderReadyUseCase setOrderReady;

    public OrderUseCases(
            GetActiveOrdersUseCase getActiveOrders,
            AcceptOrderUseCase acceptOrder,
            GetBaristaOrderHistoryUseCase getBaristaHistory,
            GetInPreparationOrdersUseCase getInPreparationOrders,
            GetPendingOrdersUseCase getPendingOrders,
            GetReadyOrdersBaristaUseCase getReadyOrdersBarista,
            SetOrderReadyUseCase setOrderReady
    ) {
        this.getActiveOrders = getActiveOrders;
        this.acceptOrder = acceptOrder;
        this.getBaristaHistory = getBaristaHistory;
        this.getInPreparationOrders = getInPreparationOrders;
        this.getPendingOrders = getPendingOrders;
        this.getReadyOrdersBarista = getReadyOrdersBarista;
        this.setOrderReady = setOrderReady;
    }

    // GETTERS

    public GetActiveOrdersUseCase getActiveOrders() {
        return getActiveOrders;
    }

    public AcceptOrderUseCase getAcceptOrder() {
        return acceptOrder;
    }

    public GetBaristaOrderHistoryUseCase getBaristaHistory() {
        return getBaristaHistory;
    }

    public GetInPreparationOrdersUseCase getInPreparationOrders() {
        return getInPreparationOrders;
    }

    public GetPendingOrdersUseCase getPendingOrders() {
        return getPendingOrders;
    }

    public GetReadyOrdersBaristaUseCase getReadyOrdersBarista() {
        return getReadyOrdersBarista;
    }

    public SetOrderReadyUseCase getSetOrderReady() {
        return setOrderReady;
    }
}

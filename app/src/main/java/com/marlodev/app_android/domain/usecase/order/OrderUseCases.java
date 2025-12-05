package com.marlodev.app_android.domain.usecase.order;

import com.marlodev.app_android.domain.usecase.order.barista.AcceptOrderUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetBaristaOrderHistoryUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.paginacion.GetBaristaOrdersPageUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetInPreparationOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetPendingOrdersUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.GetReadyOrdersBaristaUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.paginacion.GetBaristaOrdersReadyPageUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.paginacion.GetinPreparationOrdersInPreparationUseCase;
import com.marlodev.app_android.domain.usecase.order.barista.SetOrderReadyUseCase;
import com.marlodev.app_android.domain.usecase.order.cliente.GetActiveOrdersUseCase;

/**
 * OrderUseCases agrupados por contexto (Cliente, Barista).
 * Organización altamente escalable y de código limpio.
 */
public class OrderUseCases {

    public final Cliente cliente;
    public final Barista barista;

    public OrderUseCases(Cliente cliente, Barista barista) {
        this.cliente = cliente;
        this.barista = barista;
    }

    // --------------------- CONTEXTO CLIENTE ---------------------
    public static class Cliente {
        public final GetActiveOrdersUseCase getActiveOrders;

        public Cliente(GetActiveOrdersUseCase getActiveOrders) {
            this.getActiveOrders = getActiveOrders;
        }
    }

    // --------------------- CONTEXTO BARISTA ---------------------
    public static class Barista {
        public final AcceptOrderUseCase acceptOrder;
        public final GetBaristaOrdersPageUseCase getOrdersPage;
        public final GetBaristaOrderHistoryUseCase getHistory;
        public final GetInPreparationOrdersUseCase getInPreparationOrders;
        public final GetPendingOrdersUseCase getPendingOrders;
        public final GetReadyOrdersBaristaUseCase getReadyOrders;
        public final SetOrderReadyUseCase setOrderReady;
        public final GetinPreparationOrdersInPreparationUseCase getOrdersPreparationPage;
        public final GetBaristaOrdersReadyPageUseCase getBaristaOrdersReadyPage;

        public Barista(
                AcceptOrderUseCase acceptOrder,
                GetBaristaOrdersPageUseCase getOrdersPage,
                GetBaristaOrderHistoryUseCase getHistory,
                GetInPreparationOrdersUseCase getInPreparationOrders,
                GetPendingOrdersUseCase getPendingOrders,
                GetReadyOrdersBaristaUseCase getReadyOrders,
                SetOrderReadyUseCase setOrderReady,
                GetinPreparationOrdersInPreparationUseCase getOrdersPreparationPage,
                GetBaristaOrdersReadyPageUseCase getBaristaOrdersReadyPage
        ) {
            this.acceptOrder = acceptOrder;
            this.getOrdersPage = getOrdersPage;
            this.getHistory = getHistory;
            this.getInPreparationOrders = getInPreparationOrders;
            this.getPendingOrders = getPendingOrders;
            this.getReadyOrders = getReadyOrders;
            this.setOrderReady = setOrderReady;
            this.getOrdersPreparationPage = getOrdersPreparationPage;
            this.getBaristaOrdersReadyPage = getBaristaOrdersReadyPage;
        }
    }
}

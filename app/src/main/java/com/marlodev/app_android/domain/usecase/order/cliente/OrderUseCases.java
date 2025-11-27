package com.marlodev.app_android.domain.usecase.order.cliente;

public class OrderUseCases {
    private final GetActiveOrdersUseCase getActiveOrders;

    public OrderUseCases(GetActiveOrdersUseCase  getActiveOrders)
    {
        this.getActiveOrders = getActiveOrders;
    }
    public GetActiveOrdersUseCase getActiveOrders() {
        return getActiveOrders;
    }


}

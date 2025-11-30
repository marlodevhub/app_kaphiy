package com.marlodev.app_android.ui.barista.ordenes;

import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.domain.usecase.order.cliente.OrderUseCases;

public class BaristaOrderViewModel {

    private final OrderUseCases orderUseCases;

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
//        loadOrder(); // Carga inicial del carrito
    }

}

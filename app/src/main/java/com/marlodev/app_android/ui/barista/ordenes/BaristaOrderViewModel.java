package com.marlodev.app_android.ui.barista.ordenes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Result;


import java.util.List;

public class BaristaOrderViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
    }

    // Observa directamente el LiveData del repositorio (ya reactivo vía WS)
    public LiveData<Result<List<Order>>> getPendingOrders() {
        return orderUseCases.getPendingOrders().execute();
    }

    public void startPreparation(long orderId) {
        orderUseCases.getAcceptOrder().execute(orderId);
    }

    public LiveData<Result<PageResponse<Order>>> getBaristaOrdersPage(int page, int size) {
        return orderUseCases.getBaristaOrdersPage().execute(page, size);
    }


}


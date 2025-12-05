package com.marlodev.app_android.ui.barista.mas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Event;

public class BaristaOrderDetailViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    public BaristaOrderDetailViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
    }

    // Orden actual
    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    public LiveData<Order> getOrder() { return orderLiveData; }
    public void setOrder(Order order) { orderLiveData.setValue(order); }

    // Señal para avisar que la orden terminó
    private final MutableLiveData<Event<Long>> _orderFinished = new MutableLiveData<>();
    public LiveData<Event<Long>> orderFinished = _orderFinished;

    // Loading opcional
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // Cambiar estado EN_PREPARACIÓN → LISTO_PARA_ENTREGA
    public void finishOrder() {
        Order order = orderLiveData.getValue();
        if (order == null) return;

        isLoading.setValue(true);

        orderUseCases.barista.setOrderReady.execute(order.getId())
                .observeForever(result -> {
                    isLoading.setValue(false);
                    if (result != null && result.data != null) {
                        _orderFinished.setValue(new Event<>(order.getId()));
                    }
                });
    }
}

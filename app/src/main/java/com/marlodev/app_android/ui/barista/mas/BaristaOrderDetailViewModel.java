package com.marlodev.app_android.ui.barista.mas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Event;
import com.marlodev.app_android.utils.Result;

public class BaristaOrderDetailViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    public BaristaOrderDetailViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
    }

    // LiveData de la orden actual
    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    public LiveData<Order> getOrder() { return orderLiveData; }
    public void setOrder(Order order) { orderLiveData.setValue(order); }

    // LiveData para errores
    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    public LiveData<Event<String>> getErrorMessage() { return errorMessage; }

    // LiveData para avisar que la orden terminó
    private final MutableLiveData<Event<Long>> _orderFinished = new MutableLiveData<>();
    public LiveData<Event<Long>> orderFinished = _orderFinished;

    // LiveData para mostrar loading
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // Método para finalizar la preparación
    public void finishOrderAndRemove() {
        Order order = orderLiveData.getValue();
        if (order == null) return;

        isLoading.setValue(true);

        orderUseCases.barista.setOrderReady.execute(order.getId())
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result != null && result.isSuccess()) {
                        // Avisar a la Activity que la orden terminó
                        _orderFinished.setValue(new Event<>(order.getId()));
                    } else {
                        String error = (result != null && result.message != null)
                                ? result.message
                                : "Error al finalizar la orden";
                        errorMessage.setValue(new Event<>(error));
                    }
                });
    }
}

package com.marlodev.app_android.ui.barista.mas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;

public class BaristaOrderDetailViewModel extends ViewModel {

    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();

    public LiveData<Order> getOrder() { return orderLiveData; }

    public void setOrder(Order order) { orderLiveData.setValue(order); }

    // Cambiar estado de la orden a "ESPERANDO_REPARTIDOR"
    public void finishOrder() {
        Order order = orderLiveData.getValue();
        if (order != null) {
            order.setStatus(OrderStatus.ESPERANDO_REPARTIDOR);
            orderLiveData.setValue(order);
            // Llamada al backend:
            // OrderRepository.updateOrder(order);
        }
    }
}

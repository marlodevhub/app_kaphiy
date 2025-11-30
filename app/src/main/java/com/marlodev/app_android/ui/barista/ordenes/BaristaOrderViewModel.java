package com.marlodev.app_android.ui.barista.ordenes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Result;

import java.util.List;

public class BaristaOrderViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    private final MutableLiveData<Result<List<Order>>> _pendingOrders = new MutableLiveData<>();
    public LiveData<Result<List<Order>>> pendingOrders = _pendingOrders;

    private final MediatorLiveData<Result<Order>> _preparationResult = new MediatorLiveData<>();
    public LiveData<Result<Order>> preparationResult = _preparationResult;

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
        loadPendingOrders();
    }

    public void loadPendingOrders() {
        orderUseCases.getPendingOrders().execute()
                .observeForever(result -> _pendingOrders.postValue(result));
    }

    public void startPreparation(long orderId) {
        LiveData<Result<Order>> source = orderUseCases.getAcceptOrder().execute(orderId);
        _preparationResult.addSource(source, result -> {
            _preparationResult.setValue(result);
            _preparationResult.removeSource(source);
            if(result.isSuccess()){
                loadPendingOrders();
            }
        });
    }


}

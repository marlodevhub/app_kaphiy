package com.marlodev.app_android.ui.client.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Event;
import com.marlodev.app_android.utils.Result;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClientOrderViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    // LiveData para el estado de la UI (persistente)
    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);


    // LiveData para eventos de un solo uso (Toast, Navegación)
    private final MutableLiveData<Event<String>> _errorMessage = new MutableLiveData<>();

    // LiveData públicos inmutables expuestos a la UI.
    public final LiveData<List<Order>> orders = _orders;
    public final LiveData<Boolean> isLoading = _isLoading;
    public final LiveData<Event<String>> errorMessage = _errorMessage;

    public ClientOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
        loadActiveOrders();
    }

    public void refreshOrders() {
        loadActiveOrders();
    }


    private void loadActiveOrders() {
        LiveData<Result<List<Order>>> useCaseLiveData = orderUseCases.cliente.getActiveOrders.execute();
        observeUseCaseResult(useCaseLiveData, this::handleOrdersSuccess, msg -> _errorMessage.postValue(new Event<>(msg)));
    }

    private <T> void observeUseCaseResult(final LiveData<Result<T>> useCaseLiveData, final Consumer<T> onSuccess, final Consumer<String> onError) {
        if (Boolean.TRUE.equals(_isLoading.getValue())) {
            return;
        }
        _isLoading.setValue(true);

        useCaseLiveData.observeForever(new Observer<Result<T>>() {
            @Override
            public void onChanged(Result<T> result) {
                if (result.status == Result.Status.LOADING) {
                    return;
                }

                useCaseLiveData.removeObserver(this);
                _isLoading.setValue(false);

                if (result.status == Result.Status.SUCCESS) {
                    if (onSuccess != null) {
                        onSuccess.accept(result.data);
                    }
                } else if (result.status == Result.Status.ERROR) {
                    if (onError != null) {
                        onError.accept(result.message);
                    }
                }
            }
        });
    }

    private void handleOrdersSuccess(List<Order> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            _orders.postValue(Collections.emptyList());
        } else {
            // Ordenar la lista por fecha de creación descendente (los más nuevos primero).
            List<Order> sortedList = orderList.stream()
                    .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            _orders.postValue(sortedList);
        }
    }
}

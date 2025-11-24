package com.marlodev.app_android.ui.client.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.repository.OrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ClientOrderViewModel extends ViewModel {

    private final OrderRepository repository;
    private final MutableLiveData<List<OrderResponse>> activeOrders = new MutableLiveData<>();
    private final MutableLiveData<List<OrderResponse>> historyOrders = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ClientOrderViewModel(OrderRepository repository) {
        this.repository = repository;
        loadActiveOrders();
        loadHistoryOrders();
    }

    public LiveData<List<OrderResponse>> getActiveOrders() { return activeOrders; }
    public LiveData<List<OrderResponse>> getHistoryOrders() { return historyOrders; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadActiveOrders() {
        repository.getActiveOrders(new OrderRepository.SimpleCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) {
                sortOrdersByDate(result);
                activeOrders.postValue(result);
            }
            @Override
            public void onError(String message) { errorMessage.postValue(message); }
        });
    }

    public void loadHistoryOrders() {
        repository.getOrderHistory(new OrderRepository.SimpleCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) {
                sortOrdersByDate(result);
                historyOrders.postValue(result);
            }
            @Override
            public void onError(String message) { errorMessage.postValue(message); }
        });
    }

    private void sortOrdersByDate(List<OrderResponse> orders) {
        if (orders == null) return;

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        orders.sort((o1, o2) -> {
            try {
                Date d1 = isoFormat.parse(o1.getCreatedAt());
                Date d2 = isoFormat.parse(o2.getCreatedAt());
                return d2.compareTo(d1); // DESC: most recent first
            } catch (Exception e) {
                return 0;
            }
        });
    }
}

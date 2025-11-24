package com.marlodev.app_android.ui.client.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.repository.OrderRepository;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClientOrderViewModel extends ViewModel {

    private final OrderRepository repository;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final LiveData<List<Order>> activeOrders;
    private final LiveData<List<Order>> historyOrders;

    public ClientOrderViewModel(OrderRepository repository) {
        this.repository = repository;

        activeOrders = Transformations.map(
                repository.getActiveOrders(),
                result -> processResult(result)
        );

        historyOrders = Transformations.map(
                repository.getHistoryOrders(),
                result -> processResult(result)
        );
    }

    public LiveData<List<Order>> getActiveOrders() { return activeOrders; }
    public LiveData<List<Order>> getHistoryOrders() { return historyOrders; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    private List<Order> processResult(Result<List<Order>> result) {

        if (result.status == Result.Status.SUCCESS) {

            List<Order> list = result.data;
            if (list == null) return Collections.emptyList();

            list.sort(Comparator.comparing(Order::getCreatedAt).reversed());
            return list;

        } else if (result.status == Result.Status.ERROR) {
            errorMessage.setValue(result.message);
        }

        return Collections.emptyList();
    }
}


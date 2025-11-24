package com.marlodev.app_android.ui.client.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.repository.OrderRepository;
import com.marlodev.app_android.domain.DomainCallback;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.usecase.cart.CheckoutUseCase;
import com.marlodev.app_android.utils.Result;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClientOrderViewModel extends ViewModel {

    private final OrderRepository repository;
    private final CheckoutUseCase checkoutUseCase;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Result<Order>> checkoutResult = new MutableLiveData<>();

    private final LiveData<List<Order>> activeOrders;
    private final LiveData<List<Order>> historyOrders;

    public ClientOrderViewModel(OrderRepository repository, CheckoutUseCase checkoutUseCase) {
        this.repository = repository;
        this.checkoutUseCase = checkoutUseCase;

        activeOrders = Transformations.map(
                repository.getActiveOrders(),
                this::processResult
        );

        historyOrders = Transformations.map(
                repository.getHistoryOrders(),
                this::processResult
        );
    }

    public LiveData<List<Order>> getActiveOrders() { return activeOrders; }
    public LiveData<List<Order>> getHistoryOrders() { return historyOrders; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Result<Order>> getCheckoutResult() { return checkoutResult; }

    public void checkout() {
        checkoutResult.setValue(Result.loading());
        checkoutUseCase.execute(new DomainCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                checkoutResult.postValue(Result.success(data));
            }

            @Override
            public void onError(String message) {
                checkoutResult.postValue(Result.error(message));
            }
        });
    }

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

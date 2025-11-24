package com.marlodev.app_android.domain.repository;

import com.marlodev.app_android.domain.DomainCallback;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.Result;

import androidx.lifecycle.LiveData;

public interface CartRepository {
    LiveData<Result<Order>> getCart();
    LiveData<Result<Order>> addItem(CartItem item);
    LiveData<Result<Order>> updateItem(Long itemId, CartItem item);
    LiveData<Result<Order>> deleteItem(Long itemId);
    void checkout(DomainCallback<Order> callback);
}

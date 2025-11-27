package com.marlodev.app_android.domain.repository;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.utils.Result;

import java.util.List;

public interface OrderRepository {

    LiveData<Result<List<Order>>> getActiveOrders();

    LiveData<Result<List<Order>>> getOrderHistory();

    LiveData<Result<Order>> getOrderById(long orderId);

    LiveData<Result<OrderTracking>> getOrderTracking(long orderId);
}

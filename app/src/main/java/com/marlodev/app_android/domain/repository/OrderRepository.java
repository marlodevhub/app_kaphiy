package com.marlodev.app_android.domain.repository;

import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.utils.Result;

import java.util.List;

public interface OrderRepository {

    // --------------------------
// CLIENTE
// --------------------------
    LiveData<Result<Order>> getOrderById(long orderId);

    LiveData<Result<List<Order>>> getActiveOrders();

    LiveData<Result<List<Order>>> getOrderHistory();

    LiveData<Result<OrderTracking>> getOrderTracking(long orderId);

    // --------------------------
// BARISTA
// --------------------------
    LiveData<Result<List<Order>>> getQueueBarista();

    LiveData<Result<Order>> startPreparationBarista(long orderId);

    LiveData<Result<List<Order>>> getInPreparationBarista();

    LiveData<Result<Order>> markReadyBarista(long orderId);

    LiveData<Result<List<Order>>> getReadyOrdersBarista();

    LiveData<Result<List<Order>>> getMyOrdersBarista();

    // --------------------------
// DELIVERY
// --------------------------
    LiveData<Result<List<Order>>> getReadyOrdersDelivery();

    LiveData<Result<Order>> pickupOrderDelivery(long orderId);

    LiveData<Result<List<Order>>> getMyOrdersDelivery();

    LiveData<Result<List<Order>>> getHistoryOrdersDelivery();

    LiveData<Result<Void>> updateLocationDelivery(long orderId, double lat, double lng);

    LiveData<Result<Order>> cancelOrderDelivery(long orderId);

    LiveData<Result<Order>> finishOrderDelivery(long orderId);
}

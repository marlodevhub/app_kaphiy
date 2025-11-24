package com.marlodev.app_android.data.network.api;

import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.model.order.OrderTrackingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderApi {

    // Órdenes activas del usuario autenticado
    @GET("orders/active")
    Call<List<OrderResponse>> getActiveOrders();

    // Historial del usuario autenticado
    @GET("orders/history")
    Call<List<OrderResponse>> getHistoryOrders();

    // Obtiene un pedido por ID
    @GET("orders/{orderId}")
    Call<OrderResponse> getOrderById(@Path("orderId") long orderId);

    // Tracking del pedido
    @GET("orders/{orderId}/tracking")
    Call<OrderTrackingResponse> getOrderTracking(@Path("orderId") long orderId);
}

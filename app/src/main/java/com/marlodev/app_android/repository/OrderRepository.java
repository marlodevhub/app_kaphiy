package com.marlodev.app_android.repository;

import com.marlodev.app_android.dto.order.OrderResponse;
import com.marlodev.app_android.dto.order.OrderTrackingResponse;
import com.marlodev.app_android.dto.order.network.order.OrderApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrderRepository {

    private final OrderApi api;

    public interface SimpleCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public OrderRepository(OrderApi api) {
        this.api = api;
    }

    public void getOrderHistory(SimpleCallback<List<OrderResponse>> cb) {
        api.getOrderHistory().enqueue(new retrofit2.Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Error al obtener historial (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                cb.onError("No se pudo conectar al servidor");
            }
        });
    }

    public void getOrder(Long orderId, SimpleCallback<OrderResponse> cb) {
        api.getOrder(orderId).enqueue(new retrofit2.Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Error al obtener orden (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                cb.onError("No se pudo conectar al servidor");
            }
        });
    }

    public void getOrderTracking(Long orderId, SimpleCallback<OrderTrackingResponse> cb) {
        api.getOrderTracking(orderId).enqueue(new retrofit2.Callback<OrderTrackingResponse>() {
            @Override
            public void onResponse(Call<OrderTrackingResponse> call, Response<OrderTrackingResponse> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Error al obtener tracking (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<OrderTrackingResponse> call, Throwable t) {
                cb.onError("No se pudo conectar al servidor");
            }
        });
    }

    public void getActiveOrders(SimpleCallback<List<OrderResponse>> cb) {
        api.getActiveOrders().enqueue(new retrofit2.Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Error al obtener órdenes activas (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                cb.onError("No se pudo conectar al servidor");
            }
        });
    }
}

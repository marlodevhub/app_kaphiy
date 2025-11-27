package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.model.order.OrderTrackingResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepositoryImpl implements OrderRepository {

    private static final String TAG = "OrderRepositoryImpl";
    private final OrderApi api;

    public OrderRepositoryImpl(OrderApi api) {
        this.api = api;
    }

    // ======================================================
    // METODO GENÉRICO — Maneja cualquier tipo de llamada
    // ======================================================
    private <Dto, Domain> LiveData<Result<Domain>> performCallGeneric(
            Call<Dto> call,
            String action,
            Function<Dto, Domain> mapper
    ) {
        MutableLiveData<Result<Domain>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<Dto>() {
            @Override
            public void onResponse(Call<Dto> call, Response<Dto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ " + action + " completado");

                    Domain domainObj = mapper.apply(response.body());
                    liveData.postValue(Result.success(domainObj));

                } else {
                    String msg = "❌ Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<Dto> call, Throwable t) {
                String msg = "❌ Error de red al " + action + ": " + t.getMessage();
                Log.e(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

    // ======================================================
    // MÉTODOS DEL REPOSITORY
    // ======================================================

    @Override
    public LiveData<Result<List<Order>>> getActiveOrders() {
        return performCallGeneric(
                api.getActiveOrders(),
                "cargar órdenes activas",
                responses -> responses.stream()
                        .map(OrderMapper::fromResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<List<Order>>> getOrderHistory() {
        return performCallGeneric(
                api.getOrderHistory(),
                "cargar historial de órdenes",
                responses -> responses.stream()
                        .map(OrderMapper::fromResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<Order>> getOrderById(long orderId) {
        return performCallGeneric(
                api.getOrderById(orderId),
                "cargar orden por ID",
                OrderMapper::fromResponse
        );
    }

    @Override
    public LiveData<Result<OrderTracking>> getOrderTracking(long orderId) {
        return performCallGeneric(
                api.getOrderTracking(orderId),
                "cargar tracking",
                OrderMapper::fromTrackingResponse
        );
    }
}

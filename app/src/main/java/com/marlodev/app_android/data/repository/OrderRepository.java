package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.utils.Result;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional para Orders.
 * Centraliza llamadas de red, manejo de errores y mapeo a dominio.
 */
public class OrderRepository {

    private static final String TAG = "OrderRepository";
    private final OrderApi api;

    public OrderRepository(OrderApi api) {
        this.api = api;
    }

    // =====================================================
    // GENÉRICO: LLAMADAS QUE RETORNAN LISTAS
    // =====================================================
    private <T, D> LiveData<Result<List<D>>> performListCall(
            Call<List<T>> call,
            java.util.function.Function<T, D> mapper,
            String action
    ) {
        MutableLiveData<Result<List<D>>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ " + action + " exitoso");
                    List<D> domainList = response.body().stream()
                            .map(mapper)
                            .collect(Collectors.toList());
                    liveData.postValue(Result.success(domainList));
                } else {
                    String msg = "Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                String msg = "Error de red al " + action + ": " + t.getMessage();
                Log.e(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

    // =====================================================
    // GENÉRICO: LLAMADAS QUE RETORNAN UN SOLO OBJETO
    // =====================================================
    private <T, D> LiveData<Result<D>> performSingleCall(
            Call<T> call,
            java.util.function.Function<T, D> mapper,
            String action
    ) {
        MutableLiveData<Result<D>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ " + action + " exitoso");
                    D domainObj = mapper.apply(response.body());
                    liveData.postValue(Result.success(domainObj));
                } else {
                    String msg = "Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                String msg = "Error de red al " + action + ": " + t.getMessage();
                Log.e(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

    // =====================================================
    // MÉTODOS PÚBLICOS
    // =====================================================

    public LiveData<Result<List<Order>>> getActiveOrders() {
        return performListCall(api.getActiveOrders(), OrderMapper::fromResponse, "cargar órdenes activas");
    }

    public LiveData<Result<List<Order>>> getHistoryOrders() {
        return performListCall(api.getHistoryOrders(), OrderMapper::fromResponse, "cargar historial de órdenes");
    }

    public LiveData<Result<Order>> getOrderById(long orderId) {
        return performSingleCall(api.getOrderById(orderId), OrderMapper::fromResponse, "cargar orden");
    }

    public LiveData<Result<OrderTracking>> getOrderTracking(long orderId) {
        return performSingleCall(api.getOrderTracking(orderId), OrderMapper::toTrackingDomain, "cargar tracking");
    }
}

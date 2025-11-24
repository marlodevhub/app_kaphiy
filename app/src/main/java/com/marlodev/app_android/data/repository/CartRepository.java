package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.utils.CartNotifier;
import com.marlodev.app_android.utils.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio moderno para el carrito usando Result<T>
 * - Devuelve LiveData<Result<OrderResponse>> para cada operación.
 * - Maneja estados: LOADING, SUCCESS, ERROR.
 * - Conserva logs para debugging y notifica a la app de los cambios.
 */
public class CartRepository {

    private static final String TAG = "CartRepository";
    private final CartApi api;

    public CartRepository(CartApi api) {
        this.api = api;
    }

    /** Genérico para manejar llamadas a la API y LiveData */
    private LiveData<Result<OrderResponse>> performCall(Call<OrderResponse> call, String action) {
        MutableLiveData<Result<OrderResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ " + action + " exitoso");
                    liveData.postValue(Result.success(response.body()));
                    CartNotifier.notifyCartUpdated();
                } else {
                    String msg = " Error al " + action + " (" + response.code() + ")";
                    Log.d(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                String msg = "Error de red al " + action + ": " + t.getMessage();
                Log.d(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

    /** Obtiene el carrito activo */
    public LiveData<Result<OrderResponse>> getCart() {
        return performCall(api.getCart(), "cargar carrito");
    }

    /** Agrega un item al carrito */
    public LiveData<Result<OrderResponse>> addItem(CartItemRequest request) {
        return performCall(api.addItem(request), "agregar item al carrito");
    }

    /** Actualiza un item del carrito */
    public LiveData<Result<OrderResponse>> updateItem(Long itemId, CartItemRequest request) {
        return performCall(api.updateItem(itemId, request), "actualizar item del carrito");
    }

    /** Elimina un item del carrito */
    public LiveData<Result<OrderResponse>> deleteItem(Long itemId) {
        return performCall(api.deleteItem(itemId), "eliminar item del carrito");
    }

    /** Finaliza el carrito (checkout) */
    public LiveData<Result<OrderResponse>> checkout() {
        return performCall(api.checkout(), "procesar checkout");
    }
}

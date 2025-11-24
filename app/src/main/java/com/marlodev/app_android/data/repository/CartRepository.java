package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.mapper.CartItemMapper;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.utils.CartNotifier;
import com.marlodev.app_android.utils.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private static final String TAG = "CartRepository";
    private final CartApi api;

    public CartRepository(CartApi api) {
        this.api = api;
    }

    // ======================================================
    // Generic Handler para llamadas que retornan OrderResponse
    // ======================================================
    private LiveData<Result<Order>> performCall(Call<com.marlodev.app_android.data.network.model.order.OrderResponse> call,
                                                String action) {
        MutableLiveData<Result<Order>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<com.marlodev.app_android.data.network.model.order.OrderResponse>() {
            @Override
            public void onResponse(Call<com.marlodev.app_android.data.network.model.order.OrderResponse> call,
                                   Response<com.marlodev.app_android.data.network.model.order.OrderResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ " + action + " completado");

                    Order domainOrder = OrderMapper.fromResponse(response.body());
                    liveData.postValue(Result.success(domainOrder));

                    // Notificar a toda la app que el carrito cambió
                    CartNotifier.notifyCartUpdated();

                } else {
                    String msg = "❌ Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<com.marlodev.app_android.data.network.model.order.OrderResponse> call,
                                  Throwable t) {
                String msg = "❌ Error de red al " + action + ": " + t.getMessage();
                Log.e(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

    // ======================================================
    // Métodos Públicos
    // ======================================================

    /** Obtener el carrito activo */
    public LiveData<Result<Order>> getCart() {
        return performCall(api.getCart(), "cargar carrito");
    }

    /** Agregar un ítem al carrito */
    public LiveData<Result<Order>> addItem(CartItem item) {
        CartItemRequest request = CartItemMapper.toRequest(item);
        return performCall(api.addToCart(request), "agregar item al carrito");
    }

    /** Actualizar un ítem existente */
    public LiveData<Result<Order>> updateItem(Long itemId, CartItem item) {
        CartItemRequest request = CartItemMapper.toRequest(item);
        return performCall(api.updateItem(itemId, request), "actualizar item del carrito");
    }

    /** Eliminar un ítem del carrito */
    public LiveData<Result<Order>> deleteItem(Long itemId) {
        return performCall(api.removeFromCart(itemId), "eliminar item del carrito");
    }

    /** Finalizar el carrito (checkout) */
    public LiveData<Result<Order>> checkout() {
        return performCall(api.checkout(), "finalizar compra (checkout)");
    }
}

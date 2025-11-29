package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.mapper.CartItemMapper;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.CartRepository;
import com.marlodev.app_android.utils.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepositoryImpl implements CartRepository {

    private static final String TAG = "CartRepositoryImpl";
    private final CartApi api;

    public CartRepositoryImpl(CartApi api) {
        this.api = api;
    }

    private LiveData<Result<Order>> performCall(Call<OrderResponse> call, String action) {
        MutableLiveData<Result<Order>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call,
                                   Response<OrderResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ " + action + " completado");

                    Order domainOrder = OrderMapper.fromResponse(response.body());
                    liveData.postValue(Result.success(domainOrder));

                } else {
                    String msg = "❌ Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call,
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
    @Override
    public LiveData<Result<Order>> getCart() {
        return performCall(api.getCart(), "cargar carrito");
    }

    /** Agregar un ítem al carrito */
    @Override
    public LiveData<Result<Order>> addItem(CartItem item) {
        CartItemRequest request = CartItemMapper.toRequest(item);
        return performCall(api.addToCart(request), "agregar item al carrito");
    }

    /** Actualizar un ítem existente */
    @Override
    public LiveData<Result<Order>> updateItem(Long itemId, CartItem item) {
        CartItemRequest request = CartItemMapper.toRequest(item);
        return performCall(api.updateItem(itemId, request), "actualizar item del carrito");
    }

    /** Eliminar un ítem del carrito */
    @Override
    public LiveData<Result<Order>> deleteItem(Long itemId) {
        return performCall(api.removeFromCart(itemId), "eliminar item del carrito");
    }

    /** Finalizar el carrito (checkout) */
    @Override
    public LiveData<Result<Order>> checkout() {
        return performCall(api.checkout(), "finalizar la compra");
    }
}

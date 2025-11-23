package com.marlodev.app_android.repository;

import com.marlodev.app_android.dto.order.CartItemRequest;
import com.marlodev.app_android.dto.order.OrderResponse;
import com.marlodev.app_android.dto.order.network.order.CartApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private final CartApi api;

    public interface SimpleCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public CartRepository(CartApi api) {
        this.api = api;
    }

    /** Método genérico para reducir repetición de código */
    private <T> void enqueueCall(Call<T> call, SimpleCallback<T> cb, String errorPrefix) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onError(errorPrefix + " (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                cb.onError("No se pudo conectar al servidor: " + t.getMessage());
            }
        });
    }

    /** Obtiene el carrito activo */
    public void getCart(SimpleCallback<OrderResponse> cb) {
        enqueueCall(api.getCart(), cb, "Error al cargar carrito");
    }

    /** Agrega un item al carrito */
    public void addItem(CartItemRequest request, SimpleCallback<OrderResponse> cb) {
        enqueueCall(api.addItem(request), cb, "Error al agregar item");
    }

    /** Elimina un item del carrito */
    public void deleteItem(Long itemId, SimpleCallback<OrderResponse> cb) {
        enqueueCall(api.deleteItem(itemId), cb, "Error al eliminar item");
    }

    /** Actualiza un item del carrito */
    public void updateItem(Long itemId, CartItemRequest request, SimpleCallback<OrderResponse> cb) {
        enqueueCall(api.updateItem(itemId, request), cb, "Error al actualizar item");
    }

    /** Finaliza el carrito (checkout) */
    public void checkout(SimpleCallback<OrderResponse> cb) {
        enqueueCall(api.checkout(), cb, "Error al procesar pago");
    }
}

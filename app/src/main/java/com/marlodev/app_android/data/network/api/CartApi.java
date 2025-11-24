package com.marlodev.app_android.data.network.api;

import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {

    /**
     * Obtiene el carrito activo del usuario.
     */
    @GET("cart")
    Call<OrderResponse> getCart();

    /**
     * Agrega un producto al carrito.
     */
    @POST("cart/add")
    Call<OrderResponse> addToCart(@Body CartItemRequest request);

    /**
     * Elimina un ítem específico del carrito.
     * @param itemId Id del ítem dentro del pedido.
     */
    @DELETE("cart/remove/{itemId}")
    Call<OrderResponse> removeFromCart(@Path("itemId") Long itemId);

    /**
     * Actualiza un ítem del carrito (por ejemplo cantidad).
     * @param itemId Id del ítem dentro del pedido.
     * @param request Objeto con los datos a actualizar (CartItemRequest).
     */
    @PUT("cart/update/{itemId}")
    Call<OrderResponse> updateItem(@Path("itemId") Long itemId, @Body CartItemRequest request);

    /**
     * Finaliza la compra del carrito activo.
     */
    @POST("cart/checkout")
    Call<OrderResponse> checkout();
}

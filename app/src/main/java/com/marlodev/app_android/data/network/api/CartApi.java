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

    //Mostrar carrito
    @GET("cart")
    Call<OrderResponse> getCart();

    //agregar item carrito
    @POST("cart/add")
    Call<OrderResponse> addToCart(@Body CartItemRequest request);

    //Eliminar item del carrito
    @DELETE("cart/remove/{itemId}")
    Call<OrderResponse> removeFromCart(@Path("itemId") Long itemId);

    //Actualizar item del carrito
    @PUT("cart/update/{itemId}")
    Call<OrderResponse> updateItem(@Path("itemId") Long itemId, @Body CartItemRequest request);

    //Realizar compra del carrito
    @POST("cart/checkout")
    Call<OrderResponse> checkout();
}

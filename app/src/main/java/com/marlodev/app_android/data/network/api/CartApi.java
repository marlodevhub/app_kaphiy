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
    @GET("cart")
    Call<OrderResponse> getCart();

    @POST("cart/add")
    Call<OrderResponse> addItem(@Body CartItemRequest request);

    @DELETE("cart/remove/{itemId}")
    Call<OrderResponse> deleteItem(@Path("itemId") Long itemId);

    @PUT("cart/update/{itemId}")
    Call<OrderResponse> updateItem(@Path("itemId") Long itemId, @Body CartItemRequest request);

    @POST("cart/checkout")
    Call<OrderResponse> checkout();

}

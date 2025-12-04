package com.marlodev.app_android.data.network.api;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.model.order.OrderTrackingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

//    CLIENTE
    // Mostrar Órdenes activas del usuario autenticado
    @GET("orders/active")
    Call<List<OrderResponse>> getActiveOrders();

    // Historial del usuario autenticado
    @GET("orders/history")
    Call<List<OrderResponse>> getOrderHistory();

    // Obtiene un pedido por ID
    @GET("orders/{orderId}")
    Call<OrderResponse> getOrderById(@Path("orderId") long orderId);

    // Tracking del pedido
    @GET("orders/{orderId}/tracking")
    Call<OrderTrackingResponse> getOrderTracking(@Path("orderId") long orderId);



    //    BARISTA
    // Pedidos en espera (EN_ESPERA)
    @GET("barista/orders/queue")
    Call<PageResponse<OrderResponse>> getQueueBarista(
            @Query("page") int page,
            @Query("size") int size
    );
    // Pedidos en preparación del barista autenticado

    @GET("barista/orders/in-preparation")
    Call<PageResponse<OrderResponse>> getInPreparationBarista(
            @Query("page") int page,
            @Query("size") int size
    );

    // Pedidos listos para entrega (LISTO_PARA_ENTREGA)
    @GET("barista/orders/ready")
    Call<PageResponse<OrderResponse>> getReadyOrdersBarista(
            @Query("page") int page,
            @Query("size") int size
    );


    // Cambiar un pedido a LISTO_PARA_ENTREGA
    @PUT("barista/orders/{orderId}/ready")
    Call<OrderResponse> markReadyBarista(@Path("orderId") long orderId);



    // Cambiar un pedido a EN_PREPARACION
    @PUT("barista/orders/{orderId}/start")
    Call<OrderResponse> startPreparationBarista(@Path("orderId") long orderId);



    // Todos los pedidos asignados al barista
    @GET("barista/orders/my-orders")
    Call<List<OrderResponse>> getMyOrdersBarista();


//    DELIVERY
// Obtiene todos los pedidos que están listos para entrega (LISTO_PARA_ENTREGA).
    @GET("delivery/orders/ready")
    Call<List<OrderResponse>> getReadyOrdersDelivery();

    // Marca un pedido como en camino (EN_CAMINO) usando al delivery autenticado.
    @PUT("delivery/orders/{orderId}/pickup")
    Call<OrderResponse> pickupOrderDelivery(@Path("orderId") long orderId);

    // Pedidos activos del delivery
    @GET("delivery/orders/my")
    Call<List<OrderResponse>> getMyOrdersDelivery();

    // Historial de pedidos entregados
    @GET("delivery/orders/history")
    Call<List<OrderResponse>> getHistoryOrdersDelivery();

    // Actualización de ubicación en tiempo real (opcional)
    // Si tu app de delivery tiene seguimiento GPS
    @PUT("{orderId}/location")
    Call<Void> updateLocationDelivery(
            @Path("orderId") long orderId,
            @Query("lat") double lat,
            @Query("lng") double lng);

    // Cancelar o reportar un pedido, podría necesitar marcar un pedido como “no
    // pudo entregar” o reportar algún problema:
    @PUT("delivery/orders/{orderId}/cancel")
    Call<OrderResponse> cancelOrderDelivery(@Path("orderId") long orderId);

    // Marca un pedido como entregado (ENTREGADO) usando al delivery autenticado.
    @PUT("delivery/orders{orderId}/finish")
    Call<OrderResponse> finishOrderDelivery(@Path("orderId") long orderId);
}

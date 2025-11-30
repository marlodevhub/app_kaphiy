package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.mapper.CartItemMapper;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.websocket.adapter.CartItemWsAdapter;
import com.marlodev.app_android.data.network.websocket.events.CartItemWebSocketEvent;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.repository.CartRepository;
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional de Carrito
 * - LiveData seguro
 * - WebSocket en tiempo real
 * - Retrofit CRUD
 * - Loading concurrente y manejo de errores
 */
public class CartRepositoryImpl implements CartRepository {

    private static final String TAG = "CartRepositoryImpl";

    private final CartApi api;
    private final GenericWebSocketManager<CartItemWebSocketEvent> wsManager;

    private final MediatorLiveData<Order> _cartLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final AtomicInteger loadingCounter = new AtomicInteger(0);

    public final LiveData<Order> cartLiveData = _cartLiveData;
    public final LiveData<Boolean> isLoading = _isLoading;

    private final Observer<CartItemWebSocketEvent> wsObserver = this::handleWebSocketEvent;

    public CartRepositoryImpl(@NonNull CartApi api,
                              @NonNull GenericWebSocketManager<CartItemWebSocketEvent> wsManager) {
        this.api = api;
        this.wsManager = wsManager;

        if (wsManager != null) {
            _cartLiveData.addSource(wsManager.getEventLiveData(), wsObserver);
            wsManager.connect();
        }
    }

    // ---------------------------------------------------
    // WebSocket
    // ---------------------------------------------------
    private void handleWebSocketEvent(CartItemWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        CartItem updatedItem = CartItemWsAdapter.fromEvent(event);
        if (updatedItem == null) return;

        Order currentOrder = _cartLiveData.getValue();
        if (currentOrder == null) {
            currentOrder = new Order();
            currentOrder.setItems(new ArrayList<>());
        }

        List<CartItem> items = new ArrayList<>(currentOrder.getItems());

        switch (event.getAction()) {
            case "CREATE":
                items.add(updatedItem);
                Log.d(TAG, "🟢 Item CREADO vía WebSocket: " + updatedItem.getId());
                break;
            case "UPDATE":
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getId().equals(updatedItem.getId())) {
                        items.set(i, updatedItem);
                        Log.d(TAG, "🟡 Item ACTUALIZADO vía WebSocket: " + updatedItem.getId());
                        break;
                    }
                }
                break;
            case "DELETE":
                items.removeIf(i -> i.getId().equals(updatedItem.getId()));
                Log.d(TAG, "🔴 Item ELIMINADO vía WebSocket: " + updatedItem.getId());
                break;
            default:
                Log.w(TAG, "⚪ Acción desconocida en WS: " + event.getAction());
                return;
        }

        currentOrder.setItems(items);
        _cartLiveData.postValue(currentOrder);
    }

    public void shutdown() {
        if (wsManager != null) {
            _cartLiveData.removeSource(wsManager.getEventLiveData());
            wsManager.disconnect();
        }
    }

    // ---------------------------------------------------
    // Retrofit CRUD
    // ---------------------------------------------------
    private LiveData<Result<Order>> performCall(Call<OrderResponse> call, String action) {
        MutableLiveData<Result<Order>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        startLoading();

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                stopLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Order order = OrderMapper.fromResponse(response.body());
                    _cartLiveData.postValue(order);
                    liveData.postValue(Result.success(order));
                    Log.d(TAG, "✅ " + action + " completado");
                } else {
                    String msg = "❌ Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                stopLoading();
                String msg = "❌ Error de red al " + action + ": " + t.getMessage();
                Log.e(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

    @Override
    public LiveData<Result<Order>> getCart() {
        return performCall(api.getCart(), "cargar carrito");
    }

    @Override
    public LiveData<Result<Order>> addItem(CartItem item) {
        return performCall(api.addToCart(CartItemMapper.toRequest(item)), "agregar item al carrito");
    }

    @Override
    public LiveData<Result<Order>> updateItem(Long itemId, CartItem item) {
        return performCall(api.updateItem(itemId, CartItemMapper.toRequest(item)), "actualizar item del carrito");
    }

    @Override
    public LiveData<Result<Order>> deleteItem(Long itemId) {
        return performCall(api.removeFromCart(itemId), "eliminar item del carrito");
    }

    @Override
    public LiveData<Result<Order>> checkout() {
        return performCall(api.checkout(), "finalizar la compra");
    }

    // ---------------------------------------------------
    // Loading helpers
    // ---------------------------------------------------
    private void startLoading() {
        if (loadingCounter.getAndIncrement() == 0) {
            _isLoading.postValue(true);
        }
    }

    private void stopLoading() {
        if (loadingCounter.decrementAndGet() <= 0) {
            loadingCounter.set(0);
            _isLoading.postValue(false);
        }
    }
}

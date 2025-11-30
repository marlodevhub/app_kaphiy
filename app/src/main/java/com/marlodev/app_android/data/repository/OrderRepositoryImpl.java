package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.websocket.adapter.OrderWsAdapter;
import com.marlodev.app_android.data.network.websocket.events.OrderWebSocketEvent;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderTracking;
import com.marlodev.app_android.domain.repository.OrderRepository;
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Repositorio profesional de órdenes
 * - LiveData seguro
 * - WebSocket en tiempo real
 * - Retrofit CRUD
 * - Loading concurrente y manejo de errores
 */
public class OrderRepositoryImpl implements OrderRepository {

    private static final String TAG = "OrderRepositoryImpl";

    private final OrderApi api;
    private final GenericWebSocketManager<OrderWebSocketEvent> wsManager;

    private final MediatorLiveData<Order> _ordersLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final AtomicInteger loadingCounter = new AtomicInteger(0);

    public final LiveData<Order> ordersLiveData = _ordersLiveData;
    public final LiveData<Boolean> isLoading = _isLoading;

    private final Observer<OrderWebSocketEvent> wsObserver = this::handleWebSocketEvent;

    public OrderRepositoryImpl(@NonNull OrderApi api,
                               @NonNull GenericWebSocketManager<OrderWebSocketEvent> wsManager) {
        this.api = api;
        this.wsManager = wsManager;

        if (wsManager != null) {
            _ordersLiveData.addSource(wsManager.getEventLiveData(), wsObserver);
            wsManager.connect();
        }
    }

    // ---------------------------------------------------
    // WebSocket
    // ---------------------------------------------------
    private void handleWebSocketEvent(OrderWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        Order updatedOrder = OrderWsAdapter.fromEvent(event);
        if (updatedOrder == null) return;

        // Para simplificar, cada evento reemplaza o añade la orden en la lista
        Order current = _ordersLiveData.getValue();
        if (current == null) {
            current = updatedOrder;
        } else {
            switch (event.getAction()) {
                case "CREATE":
                    current = updatedOrder; // nueva orden
                    Log.d(TAG, "🟢 Orden CREADA vía WebSocket: " + updatedOrder.getId());
                    break;
                case "UPDATE":
                    current = updatedOrder; // reemplaza la orden existente
                    Log.d(TAG, "🟡 Orden ACTUALIZADA vía WebSocket: " + updatedOrder.getId());
                    break;
                case "DELETE":
                    if (current.getId().equals(updatedOrder.getId())) {
                        current = null;
                        Log.d(TAG, "🔴 Orden ELIMINADA vía WebSocket: " + updatedOrder.getId());
                    }
                    break;
                default:
                    Log.w(TAG, "⚪ Acción desconocida en WS: " + event.getAction());
                    return;
            }
        }

        _ordersLiveData.postValue(current);
    }

    public void shutdown() {
        if (wsManager != null) {
            _ordersLiveData.removeSource(wsManager.getEventLiveData());
            wsManager.disconnect();
        }
    }

    // ---------------------------------------------------
    // Retrofit CRUD (genérico)
    // ---------------------------------------------------
    private <Dto, Domain> LiveData<Result<Domain>> performCallGeneric(
            Call<Dto> call,
            String action,
            Function<Dto, Domain> mapper
    ) {
        MutableLiveData<Result<Domain>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        startLoading();
        Log.d(TAG, "🔄 " + action + "...");

        call.enqueue(new Callback<Dto>() {
            @Override
            public void onResponse(Call<Dto> call, Response<Dto> response) {
                stopLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Domain domainObj = mapper.apply(response.body());
                    liveData.postValue(Result.success(domainObj));
                    if (domainObj instanceof Order) {
                        _ordersLiveData.postValue((Order) domainObj);
                    }
                    Log.d(TAG, "✅ " + action + " completado");
                } else {
                    String msg = "❌ Error al " + action + " (" + response.code() + ")";
                    Log.e(TAG, msg);
                    liveData.postValue(Result.error(msg));
                }
            }

            @Override
            public void onFailure(Call<Dto> call, Throwable t) {
                stopLoading();
                String msg = "❌ Error de red al " + action + ": " + t.getMessage();
                Log.e(TAG, msg);
                liveData.postValue(Result.error(msg));
            }
        });

        return liveData;
    }

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

    // ---------------------------------------------------
    // Métodos públicos del repository
    // ---------------------------------------------------

    @Override
    public LiveData<Result<Order>> getOrderById(long orderId) {
        return performCallGeneric(
                api.getOrderById(orderId),
                "cargar orden por ID",
                OrderMapper::fromResponse
        );
    }

    @Override
    public LiveData<Result<List<Order>>> getActiveOrders() {
        return performCallGeneric(
                api.getActiveOrders(),
                "cargar órdenes activas",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<List<Order>>> getOrderHistory() {
        return performCallGeneric(
                api.getOrderHistory(),
                "cargar historial de órdenes",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList())
        );
    }

    @Override
    public LiveData<Result<OrderTracking>> getOrderTracking(long orderId) {
        return performCallGeneric(
                api.getOrderTracking(orderId),
                "cargar tracking",
                OrderMapper::fromTrackingResponse
        );
    }
}
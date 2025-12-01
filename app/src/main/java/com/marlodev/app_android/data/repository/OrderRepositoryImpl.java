package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // LiveData generales
    private final MediatorLiveData<Order> _ordersLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    private final AtomicInteger loadingCounter = new AtomicInteger(0);

    public final LiveData<Order> ordersLiveData = _ordersLiveData;
    public final LiveData<Boolean> isLoading = _isLoading;

    // Observers removibles
    private final Observer<OrderWebSocketEvent> wsObserver = this::handleWebSocketEvent;

    // Barista
    private final MediatorLiveData<List<Order>> _baristaOrdersLiveData = new MediatorLiveData<>();
    public final LiveData<List<Order>> baristaOrdersLiveData = _baristaOrdersLiveData;
    private final Observer<OrderWebSocketEvent> wsBaristaObserver = this::handleBaristaEvent;

    public OrderRepositoryImpl(@NonNull OrderApi api,
                               @NonNull GenericWebSocketManager<OrderWebSocketEvent> wsManager) {
        this.api = api;
        this.wsManager = wsManager;

        // Conectar WS una sola vez
        if (wsManager != null) {
            _ordersLiveData.addSource(wsManager.getEventLiveData(), wsObserver);
            _baristaOrdersLiveData.addSource(wsManager.getEventLiveData(), wsBaristaObserver);
            wsManager.connect();
        }

        // Inicializar lista Barista
        loadInitialBaristaQueue();
    }

    // ---------------------------------------------------
    // WebSocket Cliente general
    // ---------------------------------------------------
    private void handleWebSocketEvent(OrderWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        Order updatedOrder = OrderWsAdapter.fromEvent(event);
        if (updatedOrder == null) return;

        Order current = _ordersLiveData.getValue();
        if (current == null) current = updatedOrder;

        switch (event.getAction()) {
            case "CREATE":
            case "UPDATE":
                current = updatedOrder;
                Log.d(TAG, "🟢 Orden actualizada/creada vía WS: " + updatedOrder.getId());
                break;
            case "DELETE":
                if (current.getId().equals(updatedOrder.getId())) {
                    current = null;
                    Log.d(TAG, "🔴 Orden eliminada vía WS: " + updatedOrder.getId());
                }
                break;
            default:
                Log.w(TAG, "⚪ Acción desconocida WS: " + event.getAction());
                return;
        }

        _ordersLiveData.postValue(current);
    }

    // ---------------------------------------------------
    // WebSocket Barista
    // ---------------------------------------------------
    private void handleBaristaEvent(OrderWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        Order order = OrderWsAdapter.fromEvent(event);
        if (order == null) return;

        List<Order> current = _baristaOrdersLiveData.getValue();
        if (current == null) current = new ArrayList<>();

        switch (event.getAction()) {
            case "CREATE":
                current.add(0, order);
                Log.d(TAG, "🟢 Orden Barista CREADA vía WS: " + order.getId());
                break;
            case "UPDATE":
                for (int i = 0; i < current.size(); i++) {
                    if (current.get(i).getId() == order.getId()) {
                        current.set(i, order);
                        Log.d(TAG, "🟡 Orden Barista ACTUALIZADA vía WS: " + order.getId());
                        break;
                    }
                }
                break;
            case "DELETE":
                current.removeIf(o -> o.getId() == order.getId());
                Log.d(TAG, "🔴 Orden Barista ELIMINADA vía WS: " + order.getId());
                break;
        }

        _baristaOrdersLiveData.postValue(current);
    }

    // ---------------------------------------------------
    // Retrofit genérico
    // ---------------------------------------------------
    private <Dto, Domain> LiveData<Result<Domain>> performCallGeneric(
            Call<Dto> call,
            String action,
            Function<Dto, Domain> mapper
    ) {
        MutableLiveData<Result<Domain>> liveData = new MutableLiveData<>();
        liveData.postValue(Result.loading());
        startLoading();

        call.enqueue(new Callback<Dto>() {
            @Override
            public void onResponse(Call<Dto> call, Response<Dto> response) {
                stopLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Domain domainObj = mapper.apply(response.body());
                    liveData.postValue(Result.success(domainObj));
                    if (domainObj instanceof Order) _ordersLiveData.postValue((Order) domainObj);
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
        if (loadingCounter.getAndIncrement() == 0) _isLoading.postValue(true);
    }

    private void stopLoading() {
        if (loadingCounter.decrementAndGet() <= 0) {
            loadingCounter.set(0);
            _isLoading.postValue(false);
        }
    }

    // ---------------------------------------------------
    // Inicializar Barista (REST + WS)
    // ---------------------------------------------------
    private void loadInitialBaristaQueue() {
        performCallGeneric(
                api.getQueueBarista(),
                "cargar pedidos iniciales barista",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList())
        ).observeForever(result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                _baristaOrdersLiveData.postValue(result.data);
            }
        });
    }

    // ---------------------------------------------------
    // Métodos públicos - Cliente
    // ---------------------------------------------------
    @Override
    public LiveData<Result<Order>> getOrderById(long orderId) {
        return performCallGeneric(api.getOrderById(orderId), "cargar orden por ID", OrderMapper::fromResponse);
    }

    @Override
    public LiveData<Result<List<Order>>> getActiveOrders() {
        return performCallGeneric(api.getActiveOrders(), "cargar órdenes activas",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<List<Order>>> getOrderHistory() {
        return performCallGeneric(api.getOrderHistory(), "cargar historial de órdenes",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<OrderTracking>> getOrderTracking(long orderId) {
        return performCallGeneric(api.getOrderTracking(orderId), "cargar tracking",
                OrderMapper::fromTrackingResponse);
    }

    // ---------------------------------------------------
    // Métodos públicos - Barista
    // ---------------------------------------------------
    @Override
    public LiveData<Result<List<Order>>> getQueueBarista() {
        MutableLiveData<Result<List<Order>>> resultLiveData = new MutableLiveData<>();
        resultLiveData.postValue(Result.success(
                _baristaOrdersLiveData.getValue() != null ? _baristaOrdersLiveData.getValue() : new ArrayList<>()
        ));
        _baristaOrdersLiveData.observeForever(orders -> resultLiveData.postValue(Result.success(orders)));
        return resultLiveData;
    }

    @Override
    public LiveData<Result<Order>> startPreparationBarista(long orderId) {
        return performCallGeneric(api.startPreparationBarista(orderId), "iniciar preparación del pedido (barista)", OrderMapper::fromResponse);
    }

    @Override
    public LiveData<Result<Order>> markReadyBarista(long orderId) {
        return performCallGeneric(api.markReadyBarista(orderId), "marcar pedido listo (barista)", OrderMapper::fromResponse);
    }

    @Override
    public LiveData<Result<List<Order>>> getInPreparationBarista() {
        return performCallGeneric(api.getInPreparationBarista(), "cargar pedidos en preparación (barista)",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<List<Order>>> getReadyOrdersBarista() {
        return performCallGeneric(api.getReadyOrdersBarista(), "cargar pedidos listos (barista)",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<List<Order>>> getMyOrdersBarista() {
        return performCallGeneric(api.getMyOrdersBarista(), "cargar pedidos asignados al barista",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    // ---------------------------------------------------
    // Métodos públicos - Delivery
    // ---------------------------------------------------
    @Override
    public LiveData<Result<List<Order>>> getReadyOrdersDelivery() {
        return performCallGeneric(api.getReadyOrdersDelivery(), "cargar pedidos listos (delivery)",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<Order>> pickupOrderDelivery(long orderId) {
        return performCallGeneric(api.pickupOrderDelivery(orderId), "marcar pedido en camino (delivery)",
                OrderMapper::fromResponse);
    }

    @Override
    public LiveData<Result<List<Order>>> getMyOrdersDelivery() {
        return performCallGeneric(api.getMyOrdersDelivery(), "cargar pedidos activos (delivery)",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<List<Order>>> getHistoryOrdersDelivery() {
        return performCallGeneric(api.getHistoryOrdersDelivery(), "cargar historial entregados (delivery)",
                dtos -> dtos.stream().map(OrderMapper::fromResponse).collect(Collectors.toList()));
    }

    @Override
    public LiveData<Result<Void>> updateLocationDelivery(long orderId, double lat, double lng) {
        return performCallGeneric(api.updateLocationDelivery(orderId, lat, lng),
                "actualizar ubicación (delivery)", response -> null);
    }

    @Override
    public LiveData<Result<Order>> cancelOrderDelivery(long orderId) {
        return performCallGeneric(api.cancelOrderDelivery(orderId), "cancelar pedido (delivery)", OrderMapper::fromResponse);
    }

    @Override
    public LiveData<Result<Order>> finishOrderDelivery(long orderId) {
        return performCallGeneric(api.finishOrderDelivery(orderId), "marcar entregado (delivery)", OrderMapper::fromResponse);
    }

    // ---------------------------------------------------
    // Shutdown seguro
    // ---------------------------------------------------
    public void shutdown() {
        if (wsManager != null) {
            _ordersLiveData.removeSource(wsManager.getEventLiveData());
            _baristaOrdersLiveData.removeSource(wsManager.getEventLiveData());
            wsManager.disconnect();
        }
    }
}

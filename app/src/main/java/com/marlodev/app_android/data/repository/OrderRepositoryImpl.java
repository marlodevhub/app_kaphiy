package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.mapper.OrderMapper;
import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.websocket.adapter.OrderWsAdapter;
import com.marlodev.app_android.data.network.websocket.events.OrderWebSocketEvent;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
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

    // ⚠️ SEPARAR: LiveData por cada estado de orden
    private final MediatorLiveData<List<Order>> _pendingOrdersLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Order>> _inPreparationOrdersLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Order>> _readyOrdersLiveData = new MediatorLiveData<>();

    public final LiveData<List<Order>> pendingOrdersLiveData = _pendingOrdersLiveData;
    public final LiveData<List<Order>> inPreparationOrdersLiveData = _inPreparationOrdersLiveData;
    public final LiveData<List<Order>> readyOrdersLiveData = _readyOrdersLiveData;

    private final Observer<OrderWebSocketEvent> wsBaristaObserver = this::handleBaristaEvent;

    public OrderRepositoryImpl(@NonNull OrderApi api,
                               @NonNull GenericWebSocketManager<OrderWebSocketEvent> wsManager) {
        this.api = api;
        this.wsManager = wsManager;

        // Conectar WS una sola vez
        if (wsManager != null) {
            _ordersLiveData.addSource(wsManager.getEventLiveData(), wsObserver);
            _pendingOrdersLiveData.addSource(wsManager.getEventLiveData(), wsBaristaObserver);
            _inPreparationOrdersLiveData.addSource(wsManager.getEventLiveData(), wsBaristaObserver);
            _readyOrdersLiveData.addSource(wsManager.getEventLiveData(), wsBaristaObserver);
            wsManager.connect();
        }

        // Inicializar listas separadas
        _pendingOrdersLiveData.setValue(new ArrayList<>());
        _inPreparationOrdersLiveData.setValue(new ArrayList<>());
        _readyOrdersLiveData.setValue(new ArrayList<>());
    }

    // ---------------------------------------------------
    // WebSocket Cliente
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
    // WebSocket Barista - SEPARADO POR ESTADO
    // ---------------------------------------------------
    private void handleBaristaEvent(OrderWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        Order order = OrderWsAdapter.fromEvent(event);
        if (order == null) return;

        String action = event.getAction();
        Log.d(TAG, "📨 WS Barista: " + action + " | Orden #" + order.getId() + " | Estado: " + order.getStatus());

        switch (action) {
            case "CREATE":
                handleCreateOrder(order);
                break;
            case "UPDATE":
                handleUpdateOrder(order);
                break;
            case "DELETE":
                handleDeleteOrder(order);
                break;
        }
    }

    private void handleCreateOrder(Order order) {
        // Agregar a la lista correspondiente según su estado
        switch (order.getStatus()) {
            case EN_ESPERA:
                addToList(_pendingOrdersLiveData, order);
                Log.d(TAG, "🟢 Orden PENDING creada: " + order.getId());
                break;
            case EN_PREPARACION:
                addToList(_inPreparationOrdersLiveData, order);
                Log.d(TAG, "🟢 Orden IN_PREPARATION creada: " + order.getId());
                break;
            case LISTO_PARA_ENTREGA:
                addToList(_readyOrdersLiveData, order);
                Log.d(TAG, "🟢 Orden READY creada: " + order.getId());
                break;
        }
    }

    private void handleUpdateOrder(Order order) {
        // Remover de todas las listas primero
        removeFromList(_pendingOrdersLiveData, order.getId());
        removeFromList(_inPreparationOrdersLiveData, order.getId());
        removeFromList(_readyOrdersLiveData, order.getId());

        // Agregar a la lista correcta según el nuevo estado
        switch (order.getStatus()) {
            case EN_ESPERA:
                addToList(_pendingOrdersLiveData, order);
                Log.d(TAG, "🟡 Orden movida a PENDING: " + order.getId());
                break;
            case EN_PREPARACION:
                addToList(_inPreparationOrdersLiveData, order);
                Log.d(TAG, "🟡 Orden movida a IN_PREPARATION: " + order.getId());
                break;
            case LISTO_PARA_ENTREGA:
                addToList(_readyOrdersLiveData, order);
                Log.d(TAG, "🟡 Orden movida a READY: " + order.getId());
                break;
            case ENTREGADO:
            case CANCELADO:
                Log.d(TAG, "🟡 Orden finalizada, removida de listas: " + order.getId());
                break;
        }
    }

    private void handleDeleteOrder(Order order) {
        removeFromList(_pendingOrdersLiveData, order.getId());
        removeFromList(_inPreparationOrdersLiveData, order.getId());
        removeFromList(_readyOrdersLiveData, order.getId());
        Log.d(TAG, "🔴 Orden eliminada de todas las listas: " + order.getId());
    }

    private void addToList(MediatorLiveData<List<Order>> liveData, Order order) {
        List<Order> current = liveData.getValue();
        if (current == null) current = new ArrayList<>();

        // Evitar duplicados
        current.removeIf(o -> o.getId().equals(order.getId()));
        current.add(0, order); // Agregar al inicio

        liveData.postValue(new ArrayList<>(current));
    }

    private void removeFromList(MediatorLiveData<List<Order>> liveData, Long orderId) {
        List<Order> current = liveData.getValue();
        if (current == null) return;

        if (current.removeIf(o -> o.getId().equals(orderId))) {
            liveData.postValue(new ArrayList<>(current));
        }
    }

    private void updateInList(MediatorLiveData<List<Order>> liveData, Order order) {
        List<Order> current = liveData.getValue();
        if (current == null) return;

        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getId().equals(order.getId())) {
                current.set(i, order);
                liveData.postValue(new ArrayList<>(current));
                return;
            }
        }
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
    // Métodos públicos - CLIENTE
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
    // Métodos públicos - Barista (CORREGIDOS)
    // ---------------------------------------------------

    @Override// ✅
    public LiveData<Result<List<Order>>> getQueueBarista() {
        // Retornar solo las órdenes EN_ESPERA desde WebSocket
        MediatorLiveData<Result<List<Order>>> resultLiveData = new MediatorLiveData<>();
        resultLiveData.setValue(Result.success(
                _pendingOrdersLiveData.getValue() != null ? _pendingOrdersLiveData.getValue() : new ArrayList<>()
        ));
        resultLiveData.addSource(_pendingOrdersLiveData, orders ->
                resultLiveData.postValue(Result.success(orders != null ? orders : new ArrayList<>()))
        );
        return resultLiveData;
    }

    @Override // ✅
    public LiveData<Result<List<Order>>> getInPreparationBarista() {
        // Retornar solo las órdenes EN_PREPARACION desde WebSocket
        MediatorLiveData<Result<List<Order>>> resultLiveData = new MediatorLiveData<>();
        resultLiveData.setValue(Result.success(
                _inPreparationOrdersLiveData.getValue() != null ? _inPreparationOrdersLiveData.getValue() : new ArrayList<>()
        ));
        resultLiveData.addSource(_inPreparationOrdersLiveData, orders ->
                resultLiveData.postValue(Result.success(orders != null ? orders : new ArrayList<>()))
        );
        return resultLiveData;
    }

    @Override// ✅
    public LiveData<Result<List<Order>>> getReadyOrdersBarista() {
        // Retornar solo las órdenes LISTO_PARA_ENTREGA desde WebSocket
        MediatorLiveData<Result<List<Order>>> resultLiveData = new MediatorLiveData<>();
        resultLiveData.setValue(Result.success(
                _readyOrdersLiveData.getValue() != null ? _readyOrdersLiveData.getValue() : new ArrayList<>()
        ));
        resultLiveData.addSource(_readyOrdersLiveData, orders ->
                resultLiveData.postValue(Result.success(orders != null ? orders : new ArrayList<>()))
        );
        return resultLiveData;
    }

    // Paginación de pedidos en espera
    public LiveData<Result<PageResponse<Order>>> getBaristaOrdersPage(int page, int size) {
        return performCallGeneric(
                api.getQueueBarista(page, size),
                "cargar pedidos EN_ESPERA pagina " + page,
                pageResponse -> {
                    List<Order> orders = pageResponse.content.stream()
                            .map(OrderMapper::fromResponse)
                            .collect(Collectors.toList());

                    PageResponse<Order> domainPage = new PageResponse<>();
                    domainPage.content = orders;
                    domainPage.totalPages = pageResponse.totalPages;
                    domainPage.number = pageResponse.number;
                    domainPage.size = pageResponse.size;
                    domainPage.totalElements = pageResponse.totalElements;

                    return domainPage;
                }
        );
    }

    // Paginación de pedidos en preparación
    public LiveData<Result<PageResponse<Order>>> getBaristaOrdersInPreparationPage(int page, int size) {
        return performCallGeneric(
                api.getInPreparationBarista(page, size),
                "cargar pedidos EN_PREPARACION pagina " + page,
                pageResponse -> {
                    List<Order> orders = pageResponse.content.stream()
                            .map(OrderMapper::fromResponse)
                            .collect(Collectors.toList());

                    PageResponse<Order> domainPage = new PageResponse<>();
                    domainPage.content = orders;
                    domainPage.totalPages = pageResponse.totalPages;
                    domainPage.number = pageResponse.number;
                    domainPage.size = pageResponse.size;
                    domainPage.totalElements = pageResponse.totalElements;

                    return domainPage;
                }
        );
    }

    // Paginación de pedidos listos para entrega
    public LiveData<Result<PageResponse<Order>>> getBaristaOrdersReadyPage(int page, int size) {
        return performCallGeneric(
                api.getReadyOrdersBarista(page, size),
                "cargar pedidos LISTO_PARA_ENTREGA pagina " + page,
                pageResponse -> {
                    List<Order> orders = pageResponse.content.stream()
                            .map(OrderMapper::fromResponse)
                            .collect(Collectors.toList());

                    PageResponse<Order> domainPage = new PageResponse<>();
                    domainPage.content = orders;
                    domainPage.totalPages = pageResponse.totalPages;
                    domainPage.number = pageResponse.number;
                    domainPage.size = pageResponse.size;
                    domainPage.totalElements = pageResponse.totalElements;

                    return domainPage;
                }
        );
    }

    @Override // ✅
    public LiveData<Result<Order>> startPreparationBarista(long orderId) {
        return performCallGeneric(api.startPreparationBarista(orderId),
                "iniciar preparación del pedido (barista)",
                OrderMapper::fromResponse);
    }

    @Override
    public LiveData<Result<Order>> markReadyBarista(long orderId){
        return performCallGeneric(api.markReadyBarista(orderId),
                "marcar listo para entrega (barista)",
                OrderMapper::fromResponse);
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
            _pendingOrdersLiveData.removeSource(wsManager.getEventLiveData());
            _inPreparationOrdersLiveData.removeSource(wsManager.getEventLiveData());
            _readyOrdersLiveData.removeSource(wsManager.getEventLiveData());
            wsManager.disconnect();
        }
    }
}
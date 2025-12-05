package com.marlodev.app_android.ui.barista.ordenes;

import android.util.Log;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Event;
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaristaOrderViewModel extends ViewModel {

    private static final String TAG = "BaristaOrderVM";

    private final OrderUseCases orderUseCases;

    // Mapas para cada filtro
    private final Map<Long, Order> pendingOrders = new LinkedHashMap<>();
    private final Map<Long, Order> inPreparationOrders = new LinkedHashMap<>();
    private final Map<Long, Order> readyOrders = new LinkedHashMap<>();

    // Paginación por filtro
    private int pendingCurrentPage = 0;
    private int pendingTotalPages = 1;

    private int preparationCurrentPage = 0;
    private int preparationTotalPages = 1;

    private int readyCurrentPage = 0;
    private int readyTotalPages = 1;

    private final int pageSize = 20;

    // LiveData para UI
    private final MutableLiveData<List<Order>> visibleOrders = new MutableLiveData<>();
    public LiveData<List<Order>> getVisibleOrders() { return visibleOrders; }

    private final MutableLiveData<String> pageInfo = new MutableLiveData<>();
    public LiveData<String> getPageInfo() { return pageInfo; }

    private final MutableLiveData<Event<Long>> _openOrderDetail = new MutableLiveData<>();
    public LiveData<Event<Long>> openOrderDetail = _openOrderDetail;

    private final MutableLiveData<OrderStatus> currentFilter = new MutableLiveData<>(OrderStatus.EN_ESPERA);
    public LiveData<OrderStatus> getCurrentFilter() { return currentFilter; }

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    public LiveData<Event<String>> getErrorMessage() { return errorMessage; }

    // Contadores para WebSocket
    private final MutableLiveData<Integer> pendingCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> preparationCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> readyCount = new MutableLiveData<>(0);

    public LiveData<Integer> getPendingCount() { return pendingCount; }
    public LiveData<Integer> getPreparationCount() { return preparationCount; }
    public LiveData<Integer> getReadyCount() { return readyCount; }

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;

        // Cargar primera página
        loadPage(0, currentFilter.getValue());

        // Después de cargar datos iniciales, conectar WebSocket
        observeWebSocketUpdates();
    }

    // ---- FILTROS ----
    public void setFilter(OrderStatus filter) {
        if (filter == currentFilter.getValue()) return;
        currentFilter.setValue(filter);

        // Cargar la página actual del filtro seleccionado
        int pageToLoad = getCurrentPageForFilter(filter);
        loadPage(pageToLoad, filter);
    }

    // ---- PAGINACIÓN (CARGA INICIAL) ----
    public void loadPage(int page, OrderStatus filter) {
        if (page < 0) page = 0;

        isLoading.setValue(true);

        final int finalPage = page;
        final OrderStatus finalFilter = filter;

        LiveData<Result<PageResponse<Order>>> liveData;

        if (finalFilter == OrderStatus.EN_ESPERA) {
            liveData = orderUseCases.barista.getOrdersPage.execute(finalPage, pageSize);
        } else if (finalFilter == OrderStatus.EN_PREPARACION) {
            liveData = orderUseCases.barista.getOrdersPreparationPage.execute(finalPage, pageSize);
        } else if (finalFilter == OrderStatus.LISTO_PARA_ENTREGA) {
            liveData = orderUseCases.barista.getBaristaOrdersReadyPage.execute(finalPage, pageSize);
        } else {
            isLoading.setValue(false);
            return;
        }

        observeResult(liveData, data -> {
            // Actualizar paginación según el filtro
            if (finalFilter == OrderStatus.EN_ESPERA) {
                pendingCurrentPage = data.number;
                pendingTotalPages = data.totalPages;

                // Solo limpiar si es página 0 (nueva búsqueda)
                if (finalPage == 0) {
                    pendingOrders.clear();
                }
                for (Order order : data.content) {
                    pendingOrders.put(order.getId(), order);
                }
            } else if (finalFilter == OrderStatus.EN_PREPARACION) {
                preparationCurrentPage = data.number;
                preparationTotalPages = data.totalPages;

                if (finalPage == 0) {
                    inPreparationOrders.clear();
                }
                for (Order order : data.content) {
                    inPreparationOrders.put(order.getId(), order);
                }
            } else if (finalFilter == OrderStatus.LISTO_PARA_ENTREGA) {
                readyCurrentPage = data.number;
                readyTotalPages = data.totalPages;

                if (finalPage == 0) {
                    readyOrders.clear();
                }
                for (Order order : data.content) {
                    readyOrders.put(order.getId(), order);
                }
            }

            updateVisibleOrders();
            updatePageInfo();
            updateAllCounters();
            isLoading.setValue(false);

        }, error -> {
            errorMessage.setValue(new Event<>("Error al cargar órdenes: " + error));
            isLoading.setValue(false);
        });
    }

    public void nextPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        if (hasNextPage()) {
            int nextPage = getCurrentPageForFilter(filter) + 1;
            loadPage(nextPage, filter);
        }
    }

    public void previousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        if (hasPreviousPage()) {
            int prevPage = getCurrentPageForFilter(filter) - 1;
            loadPage(prevPage, filter);
        }
    }

    public void refreshCurrentPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        int currentPage = getCurrentPageForFilter(filter);
        // Forzar recarga desde página 0
        loadPage(0, filter);
    }

    private int getCurrentPageForFilter(OrderStatus filter) {
        if (filter == OrderStatus.EN_ESPERA) {
            return pendingCurrentPage;
        } else if (filter == OrderStatus.EN_PREPARACION) {
            return preparationCurrentPage;
        } else {
            return readyCurrentPage;
        }
    }

    public boolean hasPreviousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) return false;

        return getCurrentPageForFilter(filter) > 0;
    }

    public boolean hasNextPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) return false;

        int currentPage = getCurrentPageForFilter(filter);
        int totalPages = getTotalPagesForFilter(filter);

        return currentPage < totalPages - 1;
    }

    private int getTotalPagesForFilter(OrderStatus filter) {
        if (filter == OrderStatus.EN_ESPERA) {
            return pendingTotalPages;
        } else if (filter == OrderStatus.EN_PREPARACION) {
            return preparationTotalPages;
        } else {
            return readyTotalPages;
        }
    }

    // ---- ACCIONES ----
    public void startPreparation(long orderId) {
        isLoading.setValue(true);

        orderUseCases.barista.acceptOrder.execute(orderId)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result != null && result.isSuccess()) {
                        Log.d(TAG, "✅ Preparación iniciada para orden: " + orderId);
                    } else {
                        String error = result != null && result.message != null
                                ? result.message
                                : "Error al iniciar preparación";
                        errorMessage.setValue(new Event<>(error));
                    }
                });
    }

    public void markAsReady(long orderId) {
        isLoading.setValue(true);

        orderUseCases.barista.setOrderReady.execute(orderId)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result != null && result.isSuccess()) {
                        Log.d(TAG, "✅ Orden marcada como lista: " + orderId);
                    } else {
                        String error = result != null && result.message != null
                                ? result.message
                                : "Error al marcar como lista";
                        errorMessage.setValue(new Event<>(error));
                    }
                });
    }

    public void openOrderDetail(long orderId) {
        _openOrderDetail.setValue(new Event<>(orderId));
    }

    // ---- WEB SOCKET (SOLO ACTUALIZACIONES) ----
    private void observeWebSocketUpdates() {
        // Observar nuevas órdenes en espera
        observeResult(
                orderUseCases.barista.getPendingOrders.execute(),
                orders -> handleWebSocketUpdate(orders, OrderStatus.EN_ESPERA),
                null
        );

        // Observar órdenes en preparación
        observeResult(
                orderUseCases.barista.getInPreparationOrders.execute(),
                orders -> handleWebSocketUpdate(orders, OrderStatus.EN_PREPARACION),
                null
        );

        // Observar órdenes listas
        observeResult(
                orderUseCases.barista.getReadyOrders.execute(),
                orders -> handleWebSocketUpdate(orders, OrderStatus.LISTO_PARA_ENTREGA),
                null
        );
    }

    /**
     * 🔥 CORRECCIÓN CRÍTICA: WebSocket solo actualiza, NO reemplaza
     * Maneja tres tipos de operaciones:
     * 1. Nueva orden → Agregar al mapa correspondiente
     * 2. Orden actualizada → Actualizar en mapa correspondiente
     * 3. Orden eliminada → Remover de todos los mapas
     */
    private void handleWebSocketUpdate(List<Order> newOrders, OrderStatus expectedStatus) {
        Log.d(TAG, "📡 WS Update - Estado: " + expectedStatus +
                " | Cantidad: " + (newOrders != null ? newOrders.size() : 0) +
                " | Filtro actual: " + currentFilter.getValue());

        if (newOrders == null) return;

        boolean hasChanges = false;

        // DEPURACIÓN: Ver qué órdenes llegan
        for (Order order : newOrders) {
            Log.d(TAG, "🔍 Orden #" + order.getId() +
                    " - Estado actual: " + order.getStatus() +
                    " - Estado esperado: " + expectedStatus);
        }

        // Procesar cada orden recibida
        for (Order order : newOrders) {
            if (order == null || order.getId() == null) continue;

            Long orderId = order.getId();
            OrderStatus actualStatus = order.getStatus();

            // 🔥 PASO 1: Remover de TODOS los mapas primero
            boolean wasInPending = pendingOrders.remove(orderId) != null;
            boolean wasInPreparation = inPreparationOrders.remove(orderId) != null;
            boolean wasInReady = readyOrders.remove(orderId) != null;

            // 🔥 PASO 2: Agregar al mapa correcto según su estado ACTUAL
            Map<Long, Order> correctMap = getMapForStatus(actualStatus);
            correctMap.put(orderId, order);

            // Si la orden estaba en algún mapa o cambió de estado, hay cambios
            if (wasInPending || wasInPreparation || wasInReady) {
                hasChanges = true;

                Log.d(TAG, "🔄 Orden #" + orderId +
                        " movida de " +
                        (wasInPending ? "PENDING" :
                                wasInPreparation ? "PREPARATION" :
                                        wasInReady ? "READY" : "NINGUNO") +
                        " a " + actualStatus);
            }
        }

        // 🔥 CORRECCIÓN CRÍTICA: Actualizar contadores SIEMPRE
        updateAllCounters();

        // 🔥 CORRECCIÓN CRÍTICA: Actualizar UI SIEMPRE si hubo cambios
        // NO importa si el filtro actual coincide con expectedStatus
        if (hasChanges) {
            updateVisibleOrders();  // ✅ ACTUALIZAR SIEMPRE
        }

        Log.d(TAG, "✅ Estado final - Pending: " + pendingOrders.size() +
                " | Preparation: " + inPreparationOrders.size() +
                " | Ready: " + readyOrders.size());
    }
    private Map<Long, Order> getMapForStatus(OrderStatus status) {
        switch (status) {
            case EN_ESPERA:
                return pendingOrders;
            case EN_PREPARACION:
                return inPreparationOrders;
            case LISTO_PARA_ENTREGA:
                return readyOrders;
            default:
                return new LinkedHashMap<>();
        }
    }

    private void updateAllCounters() {
        pendingCount.setValue(pendingOrders.size());
        preparationCount.setValue(inPreparationOrders.size());
        readyCount.setValue(readyOrders.size());
    }

    // ---- LÓGICA INTERNA ----
    private void updateVisibleOrders() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        // Obtener las órdenes del mapa correcto
        Map<Long, Order> sourceMap = getMapForStatus(filter);

        List<Order> orderList = new ArrayList<>(sourceMap.values());

        // Ordenar por fecha de actualización (más reciente primero)
        orderList.sort(Comparator.comparing(Order::getUpdatedAt).reversed());

        visibleOrders.setValue(orderList);
    }

    private void updatePageInfo() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        int currentPage = getCurrentPageForFilter(filter);
        int totalPages = getTotalPagesForFilter(filter);

        String info = "Página " + (currentPage + 1) + " / " + Math.max(1, totalPages);
        pageInfo.setValue(info);
    }

    // ---- OBSERVADORES ----
    private <T> void observeResult(
            LiveData<Result<T>> liveData,
            Consumer<T> onSuccess,
            Consumer<String> onError
    ) {
        liveData.observeForever(result -> {
            if (result != null) {
                if (result.isSuccess() && result.data != null) {
                    onSuccess.accept(result.data);
                } else if (!result.isSuccess() && onError != null) {
                    onError.accept(result.message);
                }
            }
        });
    }

    private <T> void observeResult(LiveData<Result<T>> liveData, Consumer<T> onSuccess) {
        observeResult(liveData, onSuccess, null);
    }

    // ---- CLEANUP ----
    @Override
    protected void onCleared() {
        super.onCleared();
        pendingOrders.clear();
        inPreparationOrders.clear();
        readyOrders.clear();
    }
}
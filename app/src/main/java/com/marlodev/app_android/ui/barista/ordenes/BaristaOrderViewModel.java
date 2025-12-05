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

    // Flag para saber si es primera carga
    private boolean isFirstLoad = true;

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;

        // Observar WebSocket para actualizaciones en tiempo real
        observeWebSocketUpdates();

        // Cargar primera página
        loadPage(0, currentFilter.getValue());
    }

    // ---- FILTROS ----
    public void setFilter(OrderStatus filter) {
        if (filter == currentFilter.getValue()) return;
        currentFilter.setValue(filter);

        // Cargar la página actual del filtro seleccionado
        if (filter == OrderStatus.EN_ESPERA) {
            loadPage(pendingCurrentPage, filter);
        } else if (filter == OrderStatus.EN_PREPARACION) {
            loadPage(preparationCurrentPage, filter);
        } else if (filter == OrderStatus.LISTO_PARA_ENTREGA) {
            loadPage(readyCurrentPage, filter);
        }
    }

    // ---- PAGINACIÓN (SOLO PARA CARGA INICIAL) ----
    public void loadPage(int page, OrderStatus filter) {
        if (page < 0) page = 0;

        isLoading.setValue(true);

        // Variable final para usar en lambda
        final int finalPage = page;
        final OrderStatus finalFilter = filter;

        LiveData<Result<PageResponse<Order>>> liveData;

        if (finalFilter == OrderStatus.EN_ESPERA) {
            liveData = orderUseCases.barista.getOrdersPage.execute(finalPage, pageSize);
        } else if (finalFilter == OrderStatus.EN_PREPARACION) {
            liveData = orderUseCases.barista.getOrdersPreparationPage.execute(finalPage, pageSize);
        } else if (finalFilter == OrderStatus.LISTO_PARA_ENTREGA) {
            // Si tu UseCase se llama diferente, ajústalo aquí
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

                // Limpiar y agregar nuevas órdenes (solo en primera carga o refresh)
                if (finalPage == 0 || isFirstLoad) {
                    pendingOrders.clear();
                }
                for (Order order : data.content) {
                    pendingOrders.put(order.getId(), order);
                }
            } else if (finalFilter == OrderStatus.EN_PREPARACION) {
                preparationCurrentPage = data.number;
                preparationTotalPages = data.totalPages;

                if (finalPage == 0 || isFirstLoad) {
                    inPreparationOrders.clear();
                }
                for (Order order : data.content) {
                    inPreparationOrders.put(order.getId(), order);
                }
            } else if (finalFilter == OrderStatus.LISTO_PARA_ENTREGA) {
                readyCurrentPage = data.number;
                readyTotalPages = data.totalPages;

                if (finalPage == 0 || isFirstLoad) {
                    readyOrders.clear();
                }
                for (Order order : data.content) {
                    readyOrders.put(order.getId(), order);
                }
            }

            updateVisibleOrders();
            updatePageInfo();
            isLoading.setValue(false);
            isFirstLoad = false;
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
        loadPage(currentPage, filter);
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
                        // ✅ WebSocket actualizará automáticamente los mapas
                        // No necesitamos hacer nada aquí
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
                        // ✅ WebSocket actualizará automáticamente los mapas
                        // No necesitamos hacer nada aquí
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

    // ---- ACTUALIZACIÓN EN TIEMPO REAL (WebSocket) ----
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

    private void handleWebSocketUpdate(List<Order> newOrders, OrderStatus expectedStatus) {
        Log.d("BaristaVM", "📡 WS Update - Estado: " + expectedStatus +
                " | Cantidad: " + (newOrders != null ? newOrders.size() : 0) +
                " | Filtro actual: " + currentFilter.getValue());

        if (newOrders == null) return;
        if (newOrders == null) return; // 🔥 Cambia esto: permite listas vacías

        // Determinar qué mapa usar según el estado
        Map<Long, Order> targetMap = getMapForStatus(expectedStatus);

        // 🔥 CORRECCIÓN: Filtrar solo órdenes en el estado esperado
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : newOrders) {
            if (order.getStatus() == expectedStatus) {
                filteredOrders.add(order);
            }
        }

        // 🔥 CORRECCIÓN: Actualizar contador con las órdenes FILTRADAS
        updateCountForStatus(expectedStatus, filteredOrders.size());

        // 🔥 IMPORTANTE: Siempre limpiar y actualizar el mapa, incluso si está vacío
        targetMap.clear();
        for (Order order : filteredOrders) {
            targetMap.put(order.getId(), order);
        }

        // 🔥 CORRECCIÓN: Si estamos viendo este filtro, actualizar la UI SIEMPRE
        if (currentFilter.getValue() == expectedStatus) {
            updateVisibleOrders();
        }
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

    private void updateCountForStatus(OrderStatus status, int count) {
        switch (status) {
            case EN_ESPERA:
                pendingCount.setValue(count);
                break;
            case EN_PREPARACION:
                preparationCount.setValue(count);
                break;
            case LISTO_PARA_ENTREGA:
                readyCount.setValue(count);
                break;
        }
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
package com.marlodev.app_android.ui.barista.ordenes;

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

    // 🔥 CAMBIO: Mapas separados para cada filtro
    private final Map<Long, Order> pendingOrders = new LinkedHashMap<>();
    private final Map<Long, Order> inPreparationOrders = new LinkedHashMap<>();

    // Paginación por filtro
    private int pendingCurrentPage = 0;
    private int pendingTotalPages = 1;

    private int preparationCurrentPage = 0;
    private int preparationTotalPages = 1;

    private final int pageSize = 20;

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

    // 🔥 Flag para evitar refresh automático durante cambio de filtro
    private boolean isChangingFilter = false;

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;

        // Observers WebSocket para actualizaciones en tiempo real
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
        }
    }

    // ---- PAGINACIÓN ----
    public void loadPage(int page, OrderStatus filter) {
        if (page < 0) page = 0;

        isLoading.setValue(true);

        LiveData<Result<PageResponse<Order>>> liveData;

        if (filter == OrderStatus.EN_ESPERA) {
            liveData = orderUseCases.barista.getOrdersPage.execute(page, pageSize);
        } else if (filter == OrderStatus.EN_PREPARACION) {
            liveData = orderUseCases.barista.getOrdersPreparationPage.execute(page, pageSize);
        } else {
            isLoading.setValue(false);
            return;
        }

        observeResult(liveData, data -> {
            // Actualizar paginación según el filtro
            if (filter == OrderStatus.EN_ESPERA) {
                pendingCurrentPage = data.number;
                pendingTotalPages = data.totalPages;

                // Reemplazar órdenes EN_ESPERA
                pendingOrders.clear();
                for (Order order : data.content) {
                    pendingOrders.put(order.getId(), order);
                }
            } else if (filter == OrderStatus.EN_PREPARACION) {
                preparationCurrentPage = data.number;
                preparationTotalPages = data.totalPages;

                // Reemplazar órdenes EN_PREPARACION
                inPreparationOrders.clear();
                for (Order order : data.content) {
                    inPreparationOrders.put(order.getId(), order);
                }
            }

            updateVisibleOrders();
            updatePageInfo();
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
            int nextPage = (filter == OrderStatus.EN_ESPERA)
                    ? pendingCurrentPage + 1
                    : preparationCurrentPage + 1;
            loadPage(nextPage, filter);
        }
    }

    public void previousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        if (hasPreviousPage()) {
            int prevPage = (filter == OrderStatus.EN_ESPERA)
                    ? pendingCurrentPage - 1
                    : preparationCurrentPage - 1;
            loadPage(prevPage, filter);
        }
    }

    public void refreshCurrentPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        int currentPage = (filter == OrderStatus.EN_ESPERA)
                ? pendingCurrentPage
                : preparationCurrentPage;

        loadPage(currentPage, filter);
    }

    public boolean hasPreviousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == OrderStatus.EN_ESPERA) {
            return pendingCurrentPage > 0;
        } else {
            return preparationCurrentPage > 0;
        }
    }

    public boolean hasNextPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == OrderStatus.EN_ESPERA) {
            return pendingCurrentPage < pendingTotalPages - 1;
        } else {
            return preparationCurrentPage < preparationTotalPages - 1;
        }
    }

    // ---- ACCIONES ----
    public void startPreparation(long orderId) {
        isLoading.setValue(true);

        orderUseCases.barista.acceptOrder.execute(orderId)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result != null && result.isSuccess()) {
                        // Remover de la lista EN_ESPERA
                        pendingOrders.remove(orderId);

                        OrderStatus currentFilterValue = currentFilter.getValue();

                        if (currentFilterValue == OrderStatus.EN_ESPERA) {
                            updateVisibleOrders();

                            // Refrescar EN_ESPERA para cargar la siguiente orden
                            refreshCurrentPage();

//                            // Cambiar a EN_PREPARACION después de 500ms
//                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
//                                setFilter(OrderStatus.EN_PREPARACION);
//                            }, 500);
                        }
                    } else {
                        String error = result != null && result.message != null
                                ? result.message
                                : "Error al iniciar preparación";
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
    }

    private void handleWebSocketUpdate(List<Order> newOrders, OrderStatus expectedStatus) {
        if (newOrders == null || newOrders.isEmpty()) return;

        OrderStatus currentFilterValue = currentFilter.getValue();

        // Determinar qué mapa y página usar según el estado
        Map<Long, Order> targetMap;
        int currentPage;

        if (expectedStatus == OrderStatus.EN_ESPERA) {
            targetMap = pendingOrders;
            currentPage = pendingCurrentPage;
        } else {
            targetMap = inPreparationOrders;
            currentPage = preparationCurrentPage;
        }

        // Solo refrescar si estamos en la primera página del filtro correcto
        if (currentPage != 0) return;

        boolean hasRelevantUpdates = false;

        for (Order newOrder : newOrders) {
            // Si la orden está en el mapa actual
            if (targetMap.containsKey(newOrder.getId())) {
                // Solo mantenerla si sigue en el estado correcto
                if (newOrder.getStatus() == expectedStatus) {
                    targetMap.put(newOrder.getId(), newOrder);
                    hasRelevantUpdates = true;
                } else {
                    // La orden cambió de estado, removerla
                    targetMap.remove(newOrder.getId());
                    hasRelevantUpdates = true;
                }
            }
            // Si es una nueva orden que coincide con el estado esperado
            else if (newOrder.getStatus() == expectedStatus) {
                hasRelevantUpdates = true;
            }
        }

        if (hasRelevantUpdates && currentFilterValue == expectedStatus) {
            // Solo refrescar si estamos viendo ese filtro
            refreshCurrentPage();
        }
    }

    // ---- LÓGICA INTERNA ----
    private void updateVisibleOrders() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        // Obtener las órdenes del mapa correcto
        Map<Long, Order> sourceMap = (filter == OrderStatus.EN_ESPERA)
                ? pendingOrders
                : inPreparationOrders;

        List<Order> orderList = new ArrayList<>(sourceMap.values());

        // Ordenar por fecha de actualización (más reciente primero)
        orderList.sort(Comparator.comparing(Order::getUpdatedAt).reversed());

        visibleOrders.setValue(orderList);
    }

    private void updatePageInfo() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        int currentPage;
        int totalPages;

        if (filter == OrderStatus.EN_ESPERA) {
            currentPage = pendingCurrentPage;
            totalPages = pendingTotalPages;
        } else {
            currentPage = preparationCurrentPage;
            totalPages = preparationTotalPages;
        }

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
    }
}
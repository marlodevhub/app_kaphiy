package com.marlodev.app_android.ui.barista.ordenes;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaristaOrderViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    private final Map<Long, Order> orderMap = new LinkedHashMap<>();

    private final EnumMap<OrderStatus, Integer> pageMap = new EnumMap<>(OrderStatus.class);
    private final EnumMap<OrderStatus, Integer> totalPagesMap = new EnumMap<>(OrderStatus.class);

    private final int pageSize = 20;

    private final MediatorLiveData<List<Order>> visibleOrders = new MediatorLiveData<>();
    public LiveData<List<Order>> getVisibleOrders() { return visibleOrders; }

    private final MutableLiveData<String> pageInfo = new MutableLiveData<>();
    public LiveData<String> getPageInfo() { return pageInfo; }

    private final MutableLiveData<Event<Long>> _openOrderDetail = new MutableLiveData<>();
    public LiveData<Event<Long>> openOrderDetail = _openOrderDetail;

    private final MutableLiveData<OrderStatus> currentFilter = new MutableLiveData<>(OrderStatus.EN_ESPERA);

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;

        // Inicializar páginas por estado
        for (OrderStatus status : new OrderStatus[]{OrderStatus.EN_ESPERA, OrderStatus.EN_PREPARACION, OrderStatus.LISTO_PARA_ENTREGA}) {
            pageMap.put(status, 0);
            totalPagesMap.put(status, 1);
        }

        // Observers WebSocket / API
        observeResult(orderUseCases.barista.getPendingOrders.execute(), this::onNewOrders);
        observeResult(orderUseCases.barista.getInPreparationOrders.execute(), this::onNewOrders);

        loadPage(OrderStatus.EN_ESPERA);
    }

    public void setFilter(OrderStatus filter) {
        currentFilter.setValue(filter);
        loadPage(filter);
    }

    public void loadPage(OrderStatus filter) {
        int page = pageMap.getOrDefault(filter, 0);
        int totalPages = totalPagesMap.getOrDefault(filter, 1);

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        LiveData<Result<PageResponse<Order>>> liveData;

        switch (filter) {
            case EN_ESPERA:
                liveData = orderUseCases.barista.getOrdersPage.execute(page, pageSize);
                break;
            case EN_PREPARACION:
                liveData = orderUseCases.barista.getOrdersPreparationPage.execute(page, pageSize);
                break;
            case LISTO_PARA_ENTREGA:
                liveData = orderUseCases.barista.getReadyOrdersPage.execute(page, pageSize);
                break;
            default: return;
        }

        observeResult(liveData, data -> {
            pageMap.put(filter, data.number);
            totalPagesMap.put(filter, data.totalPages);
            updateOrders(data.content);
            emitVisiblePage();
        });
    }

    public void nextPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        int currentPage = pageMap.getOrDefault(filter, 0);
        int totalPages = totalPagesMap.getOrDefault(filter, 1);

        if (currentPage < totalPages - 1) {
            pageMap.put(filter, currentPage + 1);
            loadPage(filter);
        }
    }

    public void previousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;

        int currentPage = pageMap.getOrDefault(filter, 0);

        if (currentPage > 0) {
            pageMap.put(filter, currentPage - 1);
            loadPage(filter);
        }
    }

    public boolean hasPreviousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;
        return pageMap.getOrDefault(filter, 0) > 0;
    }

    public boolean hasNextPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;
        return pageMap.getOrDefault(filter, 0) < totalPagesMap.getOrDefault(filter, 1) - 1;
    }

    // ---- BOTONES ----
    public void startPreparation(long orderId) {
        orderUseCases.barista.acceptOrder.execute(orderId)
                .observeForever(result -> {
                    if (result != null && result.isSuccess()) {
                        Order order = orderMap.get(orderId);
                        if (order != null) {
                            order.setStatus(OrderStatus.EN_PREPARACION);
                            emitVisiblePage();
                        }
                    }
                });
    }

    // ---- LÓGICA INTERNA ----
    private void onNewOrders(List<Order> orders) {
        updateOrders(orders);
        emitVisiblePage();
    }

    private void updateOrders(List<Order> list) {
        for (Order o : list) orderMap.put(o.getId(), o);
    }

    private void emitVisiblePage() {
        List<Order> all = new ArrayList<>(orderMap.values());

        OrderStatus filter = currentFilter.getValue();
        if (filter != null) {
            List<Order> filtered = new ArrayList<>();
            for (Order o : all) if (o.getStatus() == filter) filtered.add(o);
            all = filtered;
        }

        all.sort(Comparator.comparing(Order::getUpdatedAt).reversed());

        int currentPage = pageMap.getOrDefault(filter, 0);
        int totalPages = totalPagesMap.getOrDefault(filter, 1);

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, all.size());
        List<Order> pageList = new ArrayList<>();
        if (start < end) pageList.addAll(all.subList(start, end));

        visibleOrders.setValue(pageList);
        pageInfo.setValue("Página " + (currentPage + 1) + " / " + totalPages);
    }

    private <T> void observeResult(LiveData<Result<T>> liveData, Consumer<T> onSuccess) {
        liveData.observeForever(result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                onSuccess.accept(result.data);
            }
        });
    }
}

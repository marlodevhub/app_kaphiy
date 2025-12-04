package com.marlodev.app_android.ui.barista.ordenes;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.ui.barista.mas.components.OrderItemAdapter;
import com.marlodev.app_android.utils.Event;
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaristaOrderViewModel extends ViewModel {


    private final OrderUseCases orderUseCases;

    private final Map<Long, Order> orderMap = new LinkedHashMap<>();
    private int currentPage = 0;
    private int totalPages = 1;
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

        // Observers WebSocket / API
        observeResult(orderUseCases.barista.getPendingOrders.execute(), this::onNewOrders);
        observeResult(orderUseCases.barista.getInPreparationOrders.execute(), this::onNewOrders);

        loadPage(0, currentFilter.getValue());
    }

    public void setFilter(OrderStatus filter) {
        currentFilter.setValue(filter);
        loadPage(0, filter);
    }

    public void loadPage(int page, OrderStatus filter) {
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        LiveData<Result<PageResponse<Order>>> liveData;

        if (filter == OrderStatus.EN_ESPERA) {
            liveData = orderUseCases.barista.getOrdersPage.execute(page, pageSize);
        } else if (filter == OrderStatus.EN_PREPARACION) {
            liveData = orderUseCases.barista.getOrdersPreparationPage.execute(page, pageSize);
        } else return;

        observeResult(liveData, data -> {
            currentPage = data.number;
            totalPages = data.totalPages;
            updateOrders(data.content);
            emitVisiblePage();
        });
    }


    public void nextPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;
        if (hasNextPage()) loadPage(currentPage + 1, filter);
    }

    public void previousPage() {
        OrderStatus filter = currentFilter.getValue();
        if (filter == null) filter = OrderStatus.EN_ESPERA;
        if (hasPreviousPage()) loadPage(currentPage - 1, filter);
    }

    public boolean hasPreviousPage() { return currentPage > 0; }
    public boolean hasNextPage() { return currentPage < totalPages - 1; }

    // ---- BOTONES ----
//    public void startPreparation(long orderId) {
//        Order order = orderMap.get(orderId);
//        if (order == null) return;
//
//        order.setStatus(OrderStatus.EN_PREPARACION);
//        orderUseCases.barista.acceptOrder.execute(orderId);
//
//        emitVisiblePage();
//    }


    public void startPreparation(long orderId) {
        orderUseCases.barista.acceptOrder.execute(orderId)
                .observeForever(result -> {
                    if (result != null && result.isSuccess()) {
                        Order order = orderMap.get(orderId);
                        if (order != null) {
                            order.setStatus(OrderStatus.EN_PREPARACION);
                            emitVisiblePage(); // <-- actualizar la lista inmediatamente
                        }
                    } else {
                        // Mostrar error
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

        // Filtrar primero
        OrderStatus filter = currentFilter.getValue();
        if (filter != null) {
            List<Order> filtered = new ArrayList<>();
            for (Order o : all) if (o.getStatus() == filter) filtered.add(o);
            all = filtered;
        }

        // Ordenar por updatedAt descendente
        all.sort(Comparator.comparing(Order::getUpdatedAt).reversed());

        // Paginación
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

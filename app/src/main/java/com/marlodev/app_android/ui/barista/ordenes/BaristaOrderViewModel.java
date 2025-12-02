package com.marlodev.app_android.ui.barista.ordenes;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.network.model.PageResponse;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaristaOrderViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    // Estado centralizado
    private final Map<Long, Order> orderMap = new LinkedHashMap<>();
    private int currentPage = 0;
    private int totalPages = 1;
    private final int pageSize = 20;

    // LiveData expuestos al fragment
    private final MediatorLiveData<List<Order>> visibleOrders = new MediatorLiveData<>();
    public LiveData<List<Order>> getVisibleOrders() { return visibleOrders; }

    private final MutableLiveData<String> pageInfo = new MutableLiveData<>();
    public LiveData<String> getPageInfo() { return pageInfo; }

    public boolean hasPreviousPage() { return currentPage > 0; }
    public boolean hasNextPage() { return currentPage < totalPages - 1; }

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;

        // --- WebSocket — escucha en tiempo real ---
        observeResult(orderUseCases.barista.getPendingOrders.execute(), orders -> {
            updateOrders(orders);
            currentPage = 0;
            emitVisiblePage();
        });

        // Cargar la primera página
        loadPage(0);
    }

    // --------------------
    //  MÉTODOS PÚBLICOS
    // --------------------

    public void loadPage(int page) {
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        LiveData<Result<PageResponse<Order>>> liveData = orderUseCases.barista.getOrdersPage.execute(page, pageSize);
        observeResult(liveData, data -> {
            currentPage = data.number;
            totalPages = data.totalPages;
            updateOrders(data.content);
            emitVisiblePage();
        });
    }

    public void nextPage() {
        if (hasNextPage()) loadPage(currentPage + 1);
    }

    public void previousPage() {
        if (hasPreviousPage()) loadPage(currentPage - 1);
    }

    public void startPreparation(long orderId) {
        orderUseCases.barista.acceptOrder.execute(orderId);
    }

    // --------------------
    //  LÓGICA INTERNA
    // --------------------

    private void updateOrders(List<Order> list) {
        for (Order o : list) {
            Order old = orderMap.get(o.getId());
            if (old == null || !old.equals(o)) {
                orderMap.put(o.getId(), o);
            }
        }
    }

    private void emitVisiblePage() {
        List<Order> all = new ArrayList<>(orderMap.values());
        all.sort(Comparator.comparing(Order::getUpdatedAt).reversed());

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, all.size());

        List<Order> pageList = new ArrayList<>();
        if (start < end) pageList.addAll(all.subList(start, end));

        visibleOrders.setValue(pageList);

        pageInfo.setValue("Página " + (currentPage + 1) + " / " + totalPages);
    }

    // --------------------
    //  METODO GENÉRICO PARA OBSERVAR Result<T>
    // --------------------
    private <T> void observeResult(LiveData<Result<T>> liveData, Consumer<T> onSuccess) {
        liveData.observeForever(result -> {
            if (result != null && result.isSuccess() && result.data != null) {
                onSuccess.accept(result.data);
            }
        });
    }
}

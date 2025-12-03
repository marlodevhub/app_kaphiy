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
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaristaOrderViewModel extends ViewModel {

    private final OrderUseCases orderUseCases;

    // Fuente de la verdad
    private final Map<Long, Order> orderMap = new LinkedHashMap<>();
    private int currentPage = 0;
    private int totalPages = 1;
    private final int pageSize = 20;

    private final MediatorLiveData<List<Order>> visibleOrders = new MediatorLiveData<>();
    public LiveData<List<Order>> getVisibleOrders() { return visibleOrders; }

    private final MutableLiveData<String> pageInfo = new MutableLiveData<>();
    public LiveData<String> getPageInfo() { return pageInfo; }

    private final MutableLiveData<OrderStatus> currentFilter = new MutableLiveData<>(null);

    public BaristaOrderViewModel(OrderUseCases orderUseCases) {
        this.orderUseCases = orderUseCases;
        // Inicializamos el filtro
        currentFilter.setValue(OrderStatus.EN_ESPERA);

        // WebSocket para pedidos en tiempo real (pendientes + en preparación)
        observeResult(orderUseCases.barista.getPendingOrders.execute(), this::onNewOrders);
        observeResult(orderUseCases.barista.getInPreparationOrders.execute(), this::onNewOrders);


        loadPage(0, currentFilter.getValue());
    }


    public void setFilter(OrderStatus filter) {
        currentFilter.setValue(filter);
        loadPage(0, filter); // Trae desde API/WS al presionar
    }


    // --------------------
    // PAGINACIÓN
    // --------------------
    public void loadPage(int page, OrderStatus filter) {
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        LiveData<Result<PageResponse<Order>>> liveData;

        // Selecciona la llamada según estado
        if (filter == OrderStatus.EN_ESPERA) {
            liveData = orderUseCases.barista.getOrdersPage.execute(page, pageSize);
        } else if (filter == OrderStatus.EN_PREPARACION) {
            liveData = orderUseCases.barista.getOrdersPreparationPage.execute(page, pageSize);
        } else {
            // Otros estados (listo para entrega) si aplica
//            liveData = orderUseCases.barista.getReadyOrdersPage.execute(page, pageSize);
            return;
        }

        observeResult(liveData, data -> {
            currentPage = data.number;
            totalPages = data.totalPages;
            updateOrders(data.content);
            emitVisiblePage();
        });
    }


    public void nextPage() {
        OrderStatus filter = currentFilter.getValue() != null ? currentFilter.getValue() : OrderStatus.EN_ESPERA;
        if (hasNextPage()) loadPage(currentPage + 1, filter);
    }

    public void previousPage() {
        OrderStatus filter = currentFilter.getValue() != null ? currentFilter.getValue() : OrderStatus.EN_ESPERA;
        if (hasPreviousPage()) loadPage(currentPage - 1, filter);
    }

    public boolean hasPreviousPage() { return currentPage > 0; }
    public boolean hasNextPage() { return currentPage < totalPages - 1; }

//    poner un producto en preapracion
public void startPreparation(long orderId) {
    Order order = orderMap.get(orderId);
    if (order == null) return;

    // Cambiamos el estado
    order.setStatus(OrderStatus.EN_PREPARACION);

    // Ejecutamos el UseCase (backend)
    orderUseCases.barista.acceptOrder.execute(orderId);

    // 4️⃣ Actualizar la lista interna y emitir cambios inmediatamente
    emitVisiblePage(); // Esto hará que desaparezca de la lista actual
}
    // --------------------
    //  LÓGICA INTERNA
    // --------------------
    private void onNewOrders(List<Order> orders) {
        updateOrders(orders);
        emitVisiblePage();
    }

    private void updateOrders(List<Order> list) {
        for (Order o : list) {
            orderMap.put(o.getId(), o);
        }
    }

    private void emitVisiblePage() {
        List<Order> all = new ArrayList<>(orderMap.values());
        all.sort(Comparator.comparing(Order::getUpdatedAt).reversed());

        OrderStatus filter = currentFilter.getValue();
        if (filter != null) {
            List<Order> filtered = new ArrayList<>();
            for (Order o : all) if (o.getStatus() == filter) filtered.add(o);
            all = filtered;
        }

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

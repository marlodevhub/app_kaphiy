package com.marlodev.app_android.data.network.websocket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.data.network.websocket.dto.ProductWebSocketEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Servicio WS desacoplado para Products.
 * Mantiene LiveData de productos actualizado automáticamente a partir de eventos WS.
 */
public class ProductWebSocketService {

    private final GenericWebSocketManager<ProductWebSocketEvent> wsManager;
    private final MutableLiveData<List<Product>> _productsLive = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Product>> productsLive = _productsLive;

    private final Observer<ProductWebSocketEvent> wsObserver = this::handleEvent;

    public ProductWebSocketService(GenericWebSocketManager<ProductWebSocketEvent> wsManager) {
        this.wsManager = wsManager;
        this.wsManager.getEventLiveData().observeForever(wsObserver);
    }

    private void handleEvent(ProductWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Product> currentList = _productsLive.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        List<Product> updatedList = applyEventToList(event, currentList);
        _productsLive.postValue(updatedList);
    }

    /** Método estático para aplicar un evento a una lista existente */
    public static List<Product> applyEventToList(ProductWebSocketEvent event, List<Product> currentList) {
        if (currentList == null) currentList = new ArrayList<>();
        Product product = Product.fromWebSocketEvent(event);
        List<Product> updatedList = new ArrayList<>(currentList);

        switch (event.getAction()) {
            case "CREATE":
                if (updatedList.stream().noneMatch(p -> Objects.equals(p.getId(), product.getId())))
                    updatedList.add(0, product);
                break;
            case "UPDATE":
            case "IMAGES_UPDATE":
                boolean found = false;
                for (int i = 0; i < updatedList.size(); i++) {
                    if (Objects.equals(updatedList.get(i).getId(), product.getId())) {
                        updatedList.set(i, product);
                        found = true;
                        break;
                    }
                }
                if (!found && event.getAction().equals("IMAGES_UPDATE"))
                    updatedList.add(0, product);
                break;
            case "DELETE":
                updatedList.removeIf(p -> Objects.equals(p.getId(), product.getId()));
                break;
        }
        return updatedList;
    }

    /** Conectar WS */
    public void connect() {
        wsManager.connect();
    }

    /** Desconectar WS y liberar recursos */
    public void disconnect() {
        wsManager.getEventLiveData().removeObserver(wsObserver);
        wsManager.disconnect();
    }
}

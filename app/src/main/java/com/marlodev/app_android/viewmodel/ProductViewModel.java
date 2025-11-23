
package com.marlodev.app_android.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.dto.product.ProductWebSocketEvent;
import com.marlodev.app_android.dto.order.network.ApiClient;
import com.marlodev.app_android.dto.order.network.GenericWebSocketManager;
import com.marlodev.app_android.dto.order.network.ProductApiService;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.utils.SessionManager;

import java.util.List;

/**
 * ViewModel profesional para Product.
 */
public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository repository;
    private final LiveData<List<Product>> products;
    private final LiveData<Boolean> isLoading;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        ProductApiService apiService = ApiClient.getClient(application)
                .create(ProductApiService.class);

        SessionManager session = SessionManager.getInstance(application);
        String token = session.getToken();

        String wsUrl = "wss://ecommerce-backend-o9y5.onrender.com/ws-products";
//        String wsUrl = "ws://10.0.2.2:8080/ws-products";


        GenericWebSocketManager<ProductWebSocketEvent> wsManager =
                new GenericWebSocketManager<>(wsUrl, token, "/topic/products", ProductWebSocketEvent.class);

        repository = new ProductRepository(apiService, wsManager);

        products = repository.products;
        isLoading = repository.isLoading;

        // Carga inicial
        repository.loadProducts();
        // ⚡ Conexión WebSocket aquí
        repository.connectWebSocket();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Product> getProductById(long id) {
        return repository.getProductById(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.disconnectWebSocket();
    }
}

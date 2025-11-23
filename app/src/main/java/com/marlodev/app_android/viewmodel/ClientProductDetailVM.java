package com.marlodev.app_android.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.dto.order.CartItemRequest;
import com.marlodev.app_android.dto.order.OrderResponse;
import com.marlodev.app_android.dto.order.network.ApiClient;
import com.marlodev.app_android.dto.order.network.ProductApiService;
import com.marlodev.app_android.dto.order.network.GenericWebSocketManager;
import com.marlodev.app_android.dto.order.network.order.CartApi;
import com.marlodev.app_android.dto.product.ProductWebSocketEvent;
import com.marlodev.app_android.repository.CartRepository;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.utils.CartNotifier;
import com.marlodev.app_android.utils.SessionManager;

import retrofit2.Retrofit;

public class ClientProductDetailVM extends AndroidViewModel {

    private final ProductRepository productRepo;
    private final CartRepository cartRepo;
    private final SessionManager session;

    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> navigationEvent = new MutableLiveData<>();

    public ClientProductDetailVM(@NonNull Application application) {
        super(application);

        Retrofit client = ApiClient.getClient(application);
        this.session = SessionManager.getInstance(application);

        // WS Manager para actualizaciones en tiempo real
        String wsUrl = "ws://10.0.2.2:8080/ws/products";
        String token = session.isLoggedIn() ? session.getToken() : null;
        String topic = "/topic/products";

        GenericWebSocketManager<ProductWebSocketEvent> wsManager =
                new GenericWebSocketManager<>(wsUrl, token, topic, ProductWebSocketEvent.class);

        this.productRepo = new ProductRepository(client.create(ProductApiService.class), wsManager);
        this.cartRepo = new CartRepository(client.create(CartApi.class));
    }

    // Getters
    public LiveData<Product> getProduct() { return product; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getNavigationEvent() { return navigationEvent; }

    /** Cargar detalles del producto */
    public void loadProduct(long productId) {
        isLoading.postValue(true);

        productRepo.getProductById(productId).observeForever(p -> {
            if (p != null) {
                product.postValue(p);
            } else {
                errorMessage.postValue("No se encontró el producto");
            }
            isLoading.postValue(false);
        });
    }

    /** Agregar producto al carrito */
    public void addToCart(long productId, long quantity) {

        if (!session.isLoggedIn()) {
            navigationEvent.postValue("GO_LOGIN_FIRST:" + productId);
            return;
        }

        Product current = product.getValue();
        if (current == null) {
            errorMessage.postValue("El producto aún no se ha cargado");
            return;
        }

        CartItemRequest request = new CartItemRequest(
                productId,
                null,
                null,
                (int) quantity,
                current.getPrice()
        );


        cartRepo.addItem(request, new CartRepository.SimpleCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                navigationEvent.postValue("ADDED_TO_CART");
                CartNotifier.notifyCartUpdated(); // 🔹 notificar cambio global
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    /** WS */
    public void connectWebSocket() {
        productRepo.connectWebSocket();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        productRepo.disconnectWebSocket();
    }
}





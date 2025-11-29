package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.api.ProductApiService;
import com.marlodev.app_android.data.network.mapper.ProductMapper;
import com.marlodev.app_android.data.network.model.product.ProductResponse;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.websocket.adapter.ProductWsAdapter;
import com.marlodev.app_android.data.network.websocket.dto.ProductWebSocketEvent;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio optimizado de productos.
 * Maneja:
 *  - LiveData seguro y observable
 *  - WebSocket sin memory leaks
 *  - CRUD con Retrofit
 *  - Loading concurrente
 */
public class ProductRepositoryImpl implements ProductRepository {

    private static final String TAG = "ProductRepositoryImpl";

    private final ProductApiService apiService;
    private final GenericWebSocketManager<ProductWebSocketEvent> wsManager;

    // LiveData internos
    private final MediatorLiveData<List<Product>> _products = new MediatorLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    // LiveData públicos
    public final LiveData<List<Product>> products = _products;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;

    // Contador de operaciones concurrentes para isLoading
    private final AtomicInteger loadingCounter = new AtomicInteger(0);

    // Observer para WebSocket
    private final androidx.lifecycle.Observer<ProductWebSocketEvent> webSocketObserver = this::handleWebSocketEvent;

    public ProductRepositoryImpl(@NonNull ProductApiService apiService,
                                 GenericWebSocketManager<ProductWebSocketEvent> wsManager) {
        this.apiService = apiService;
        this.wsManager = wsManager;

        // Inicializamos la lista vacía para evitar nulls
        _products.setValue(new ArrayList<>());

        // Observamos WebSocket si existe
        if (this.wsManager != null) {
            _products.addSource(this.wsManager.getEventLiveData(), webSocketObserver);
        }

        // Carga inicial
        loadProducts();
    }

    // ---------------------------------------------------
    // WebSocket
    // ---------------------------------------------------
    public void connectWebSocket() {
        if (wsManager != null) wsManager.connect();
    }

    public void disconnectWebSocket() {
        if (wsManager != null) wsManager.disconnect();
    }

    private void handleWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Product> currentList = _products.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        Product updatedProduct = ProductWsAdapter.fromEvent(event);
        if (updatedProduct == null || updatedProduct.getId() == null) return;

        List<Product> newList = new ArrayList<>(currentList);
        long productId = updatedProduct.getId();

        switch (event.getAction()) {
            case "CREATE":
                if (newList.stream().noneMatch(p -> Objects.equals(p.getId(), productId))) {
                    newList.add(0, updatedProduct);
                    Log.d(TAG, "🟢 Producto CREADO vía WebSocket: " + updatedProduct.getName());
                }
                break;

            case "UPDATE":
            case "IMAGES_UPDATE":
                boolean found = false;
                for (int i = 0; i < newList.size(); i++) {
                    if (Objects.equals(newList.get(i).getId(), productId)) {
                        newList.set(i, updatedProduct);
                        found = true;
                        break;
                    }
                }
                if (!found && event.getAction().equals("IMAGES_UPDATE")) {
                    newList.add(0, updatedProduct);
                }
                Log.d(TAG, "🟡 Producto ACTUALIZADO vía WebSocket: " + updatedProduct.getName());
                break;

            case "DELETE":
                if (newList.removeIf(p -> Objects.equals(p.getId(), productId))) {
                    Log.d(TAG, "🔴 Producto ELIMINADO vía WebSocket: " + productId);
                }
                break;

            default:
                Log.w(TAG, "⚪ Acción desconocida en WebSocket: " + event.getAction());
                return;
        }

        _products.postValue(newList);
    }

    public void shutdown() {
        if (wsManager != null) {
            _products.removeSource(wsManager.getEventLiveData());
            disconnectWebSocket();
        }
    }

    // ---------------------------------------------------
    // CRUD REST
    // ---------------------------------------------------

    @Override
    public void createProduct(RequestBody productJson, MultipartBody.Part[] images) {
        executeCall(apiService.createProduct(productJson, images), new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!response.isSuccessful() || response.body() == null)
                    _errorMessage.postValue("Error al crear producto (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                _errorMessage.postValue("Error al crear: " + t.getMessage());
            }
        });
    }

    @Override
    public void updateProduct(long productId, RequestBody productJson, MultipartBody.Part[] images) {
        executeCall(apiService.updateProduct(productId, productJson, images), new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!response.isSuccessful())
                    _errorMessage.postValue("Error al actualizar producto (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                _errorMessage.postValue("Error al actualizar: " + t.getMessage());
            }
        });
    }

    @Override
    public void deleteProduct(long productId) {
        executeCall(apiService.deleteProduct(productId), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful())
                    _errorMessage.postValue("Error al eliminar producto (" + response.code() + ")");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _errorMessage.postValue("Error al eliminar: " + t.getMessage());
            }
        });
    }

    @Override
    public LiveData<Product> getProductById(long id) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();

        List<Product> currentList = _products.getValue();
        if (currentList != null) {
            Product local = currentList.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            if (local != null) {
                liveData.postValue(local);
                return liveData;
            }
        }

        executeCall(apiService.getProductById(id), new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                liveData.postValue(response.isSuccessful() && response.body() != null
                        ? ProductMapper.fromResponse(response.body())
                        : null);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    @Override
    public LiveData<List<Product>> getAllProducts() {
        loadProducts();
        return products;
    }

    public void loadProducts() {
        executeCall(apiService.getProducts(), new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    _errorMessage.postValue("Error al cargar productos (" + response.code() + ")");
                    return;
                }
                _products.postValue(ProductMapper.fromResponseList(response.body()));
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _errorMessage.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    // ---------------------------------------------------
    // Helpers
    // ---------------------------------------------------

    // Ejecuta cualquier llamada Retrofit con loading automático
    private <T> void executeCall(Call<T> call, Callback<T> callback) {
        startLoading();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                stopLoading();
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                stopLoading();
                callback.onFailure(call, t);
            }
        });
    }

    private void startLoading() {
        if (loadingCounter.getAndIncrement() == 0) {
            _isLoading.postValue(true);
        }
    }

    private void stopLoading() {
        if (loadingCounter.decrementAndGet() <= 0) {
            loadingCounter.set(0);
            _isLoading.postValue(false);
        }
    }
}
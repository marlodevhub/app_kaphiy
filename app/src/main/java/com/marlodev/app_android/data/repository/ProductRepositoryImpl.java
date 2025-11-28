package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.marlodev.app_android.data.network.websocket.adapter.ProductWsAdapter;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.data.network.mapper.ProductMapper;
import com.marlodev.app_android.data.network.model.product.ProductResponse;
import com.marlodev.app_android.data.network.websocket.dto.ProductWebSocketEvent;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.api.ProductApiService;
import com.marlodev.app_android.domain.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepositoryImpl implements ProductRepository {

    private static final String TAG = "ProductRepositoryImpl";

    private final ProductApiService apiService;
    private final GenericWebSocketManager<ProductWebSocketEvent> wsManager;

    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    public final LiveData<List<Product>> products = _products;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;

    private final Observer<ProductWebSocketEvent> webSocketObserver;

    // ---------------------------------------------------
    // 🔹 CONSTRUCTOR
    // ---------------------------------------------------
    public ProductRepositoryImpl(
            ProductApiService apiService,
            GenericWebSocketManager<ProductWebSocketEvent> wsManager
    ) {
        this.apiService = apiService;
        this.wsManager = wsManager;

        this.webSocketObserver = this::handleWebSocketEvent;

        if (this.wsManager != null) {
            this.wsManager.getEventLiveData().observeForever(webSocketObserver);
        }
    }

    @Override
    public LiveData<List<Product>> getAllProducts() {
        loadProducts();
        return products;
    }

    // ---------------------------------------------------
    // 🔹 WEBSOCKET
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

        long productId = updatedProduct.getId();

        List<Product> newList = new ArrayList<>(currentList);

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
        if (wsManager != null && webSocketObserver != null) {
            wsManager.getEventLiveData().removeObserver(webSocketObserver);
        }
        disconnectWebSocket();
    }

    // ---------------------------------------------------
    // 🔹 REST - CRUD
    // ---------------------------------------------------

    @Override
    public void createProduct(RequestBody productJson, MultipartBody.Part[] images) {
        apiService.createProduct(productJson, images)
                .enqueue(new Callback<ProductResponse>() {

                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if (!response.isSuccessful()) {
                            _errorMessage.postValue("Error al crear producto (" + response.code() + ")");
                            return;
                        }
                        Log.d(TAG, "Petición CREATE enviada al servidor.");
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        _errorMessage.postValue("Error al crear: " + t.getMessage());
                    }
                });
    }

    @Override
    public void updateProduct(long productId, RequestBody productJson, MultipartBody.Part[] images) {
        apiService.updateProduct(productId, productJson, images)
                .enqueue(new Callback<ProductResponse>() {

                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if (!response.isSuccessful()) {
                            _errorMessage.postValue("Error al actualizar producto (" + response.code() + ")");
                            return;
                        }
                        Log.d(TAG, "Petición UPDATE enviada para ID: " + productId);
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        _errorMessage.postValue("Error al actualizar: " + t.getMessage());
                    }
                });
    }

    @Override
    public void deleteProduct(long productId) {
        apiService.deleteProduct(productId)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            _errorMessage.postValue("Error al eliminar producto (" + response.code() + ")");
                            return;
                        }
                        Log.d(TAG, "Petición DELETE enviada para ID: " + productId);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        _errorMessage.postValue("Error al eliminar: " + t.getMessage());
                    }
                });
    }

    public void loadProducts() {
        _isLoading.postValue(true);

        apiService.getProducts().enqueue(new Callback<List<ProductResponse>>() {

            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                _isLoading.postValue(false);

                if (!response.isSuccessful() || response.body() == null) {
                    _errorMessage.postValue("Error al cargar productos (" + response.code() + ")");
                    return;
                }

                List<Product> domainList = ProductMapper.fromResponseList(response.body());

                _products.postValue(domainList);

                Log.d(TAG, "Productos cargados correctamente: " + domainList.size());
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de conexión: " + t.getMessage());
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

        apiService.getProductById(id).enqueue(new Callback<ProductResponse>() {

            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                liveData.postValue(
                        response.isSuccessful() && response.body() != null
                                ? ProductMapper.fromResponse(response.body())
                                : null
                );
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }
}

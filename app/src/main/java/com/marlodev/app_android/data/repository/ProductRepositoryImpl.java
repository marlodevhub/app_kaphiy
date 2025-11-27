package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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
    private final Observer<ProductWebSocketEvent> webSocketObserver;

    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    public final LiveData<List<Product>> products = _products;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;

    // constructor con WebSocket
    public ProductRepositoryImpl(ProductApiService apiService,
                                 GenericWebSocketManager<ProductWebSocketEvent> wsManager) {
        this.apiService = apiService;
        this.wsManager = wsManager;
        this.webSocketObserver = this::handleWebSocketEvent;
        this.wsManager.getEventLiveData().observeForever(webSocketObserver);
    }

    // constructor sin WebSocket
    public ProductRepositoryImpl(ProductApiService apiService) {
        this.apiService = apiService;
        this.wsManager = null;
        this.webSocketObserver = null;
    }

    // -----------------------------
    // IMPLEMENTACIÓN DE LA INTERFAZ
    // -----------------------------

    @Override
    public LiveData<List<Product>> getAllProducts() {
        loadProducts(); // Asegura que se inicie la carga si aún no se ha hecho.
        return products; // Devuelve el LiveData público existente.
    }

    // -----------------------------    
    // WEBSOCKET
    // -----------------------------
    public void connectWebSocket() {
        wsManager.connect();
    }
    public void disconnectWebSocket() {
        wsManager.disconnect();
    }
    private void handleWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Product> currentList = _products.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>(); // Asegurarse de no operar sobre nulos
        }

        Product product = Product.fromWebSocketEvent(event);
        if (product.getId() == null) return;

        List<Product> updatedList = new ArrayList<>(currentList);
        long productId = product.getId();

        switch (event.getAction()) {
            case "CREATE":
                if (updatedList.stream().noneMatch(p -> Objects.equals(p.getId(), productId))) {
                    updatedList.add(0, product);
                    Log.d(TAG, "🟢 Producto CREADO: " + product.getName());
                }
                break;

            case "UPDATE":
            case "IMAGES_UPDATE":
                boolean found = false;
                for (int i = 0; i < updatedList.size(); i++) {
                    if (Objects.equals(updatedList.get(i).getId(), productId)) {
                        updatedList.set(i, product); 
                        found = true;
                        break;
                    }
                }
                if (!found && event.getAction().equals("IMAGES_UPDATE")) {
                    updatedList.add(0, product);
                }
                Log.d(TAG, "🟡 Producto ACTUALIZADO: " + product.getName());
                break;

            case "DELETE":
                if (updatedList.removeIf(p -> Objects.equals(p.getId(), productId))) {
                    Log.d(TAG, "🔴 Producto ELIMINADO: " + productId);
                }
                break;

            default:
                Log.w(TAG, "⚪ Acción desconocida: " + event.getAction());
                return;
        }

        _products.postValue(updatedList);
    }

    public void shutdown() {
        wsManager.getEventLiveData().removeObserver(webSocketObserver);
        disconnectWebSocket();
    }

    // -----------------------------    
    // REST API - CRUD COMPLETO
    // -----------------------------

    @Override
    public void createProduct(RequestBody productJson, MultipartBody.Part[] images) {
        apiService.createProduct(productJson, images).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Petición CREATE enviada con éxito.");
                } else {
                    _errorMessage.postValue("Error al crear producto (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                _errorMessage.postValue("Error de red: " + t.getMessage());
            }
        });
    }

    @Override
    public void updateProduct(long productId, RequestBody productJson, MultipartBody.Part[] images) {
        apiService.updateProduct(productId, productJson, images).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Petición UPDATE para el producto " + productId + " enviada con éxito.");
                } else {
                    _errorMessage.postValue("Error al actualizar producto (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                _errorMessage.postValue("Error de red al actualizar: " + t.getMessage());
            }
        });
    }

    public void loadProducts() {
        _isLoading.postValue(true);
        apiService.getProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _products.postValue(ProductMapper.fromResponseList(response.body()));
                    Log.d(TAG, "✅ Productos cargados: " + response.body().size());
                } else {
                    _errorMessage.postValue("Error al cargar productos (" + response.code() + ")");
                    Log.e(TAG, "⚠️ Error al cargar productos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "❌ Falló la carga: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public LiveData<Product> getProductById(long id) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();
        List<Product> list = _products.getValue();
        if (list != null) {
            Product local = list.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            if (local != null) {
                liveData.postValue(local);
                return liveData;
            }
        }

        apiService.getProductById(id).enqueue(new Callback<ProductResponse>() {
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
    public void deleteProduct(long productId) {
        apiService.deleteProduct(productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Petición DELETE para el producto " + productId + " enviada con éxito.");
                } else {
                    _errorMessage.postValue("Error al eliminar producto (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _errorMessage.postValue("Error de red al eliminar: " + t.getMessage());
            }
        });
    }
}

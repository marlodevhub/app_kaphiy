package com.marlodev.app_android.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.model.Banner;
import com.marlodev.app_android.data.network.mapper.BannerMapper;
import com.marlodev.app_android.data.network.model.banner.BannerRequest;
import com.marlodev.app_android.data.network.model.banner.BannerResponse;
import com.marlodev.app_android.data.network.websocket.events.BannerWebSocketEvent;
import com.marlodev.app_android.data.network.api.BannerApiService;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * BannerRepositoryImpl profesional.
 * Gestiona REST API + WebSocket.
 * Mantiene LiveData sincronizado y convierte automáticamente entre DTOs y dominio.
 */
public class BannerRepositoryImpl {

    private static final String TAG = "BannerRepositoryImpl";

    private final BannerApiService apiService;
    private final GenericWebSocketManager<BannerWebSocketEvent> wsManager;

    private final MutableLiveData<List<Banner>> _banners = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    public final LiveData<List<Banner>> banners = _banners;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;

    public BannerRepositoryImpl(BannerApiService apiService,
                                GenericWebSocketManager<BannerWebSocketEvent> wsManager) {
        this.apiService = apiService;
        this.wsManager = wsManager;
    }

    // -----------------------------
    // WebSocket
    // -----------------------------
    public void connectWebSocket() {
        wsManager.connect();
    }

    public void disconnectWebSocket() {
        wsManager.disconnect();
    }

    public void observeWebSocketEvents(@NonNull LifecycleOwner owner) {
        wsManager.getEventLiveData().observe(owner, this::handleWebSocketEvent);
    }

    private void handleWebSocketEvent(BannerWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Banner> currentList = _banners.getValue() != null ? _banners.getValue() : new ArrayList<>();
        Banner banner = Banner.builder()
                .id(event.getId())
                .title(event.getTitle())
                .order(event.getOrder())
                .url(event.getUrl())
                .publicId(event.getPublicId())
                .build();

        List<Banner> updatedList = new ArrayList<>();
        boolean found = false;

        switch (event.getAction()) {
            case "CREATE":
                if (currentList.stream().noneMatch(b -> b.getId().equals(banner.getId()))) {
                    updatedList.add(banner);
                    updatedList.addAll(currentList);
                    Log.d(TAG, "🟢 Banner CREADO: " + banner.getTitle());
                } else {
                    updatedList.addAll(currentList);
                }
                break;

            case "UPDATE":
                for (Banner b : currentList) {
                    if (b.getId().equals(banner.getId())) {
                        updatedList.add(banner);
                        found = true;
                    } else {
                        updatedList.add(b);
                    }
                }
                if (!found) updatedList.add(banner);
                Log.d(TAG, "🟡 Banner ACTUALIZADO: " + banner.getTitle());
                break;

            case "DELETE":
                for (Banner b : currentList) {
                    if (!b.getId().equals(banner.getId())) updatedList.add(b);
                }
                Log.d(TAG, "🔴 Banner ELIMINADO: " + banner.getId());
                break;

            default:
                updatedList.addAll(currentList);
                Log.w(TAG, "⚪ Acción desconocida: " + event.getAction());
        }

        _banners.postValue(updatedList);
    }

    // -----------------------------
    // REST API
    // -----------------------------
    public void loadBanners() {
        _isLoading.postValue(true);
        apiService.listarBanners().enqueue(new Callback<List<BannerResponse>>() {
            @Override
            public void onResponse(Call<List<BannerResponse>> call, Response<List<BannerResponse>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _banners.postValue(BannerMapper.fromResponseList(response.body()));
                    Log.d(TAG, "✅ Banners cargados: " + response.body().size());
                } else {
                    _errorMessage.postValue("Error al cargar banners (" + response.code() + ")");
                    Log.e(TAG, "⚠️ Error al cargar banners: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BannerResponse>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "❌ Falló la carga: " + t.getMessage(), t);
            }
        });
    }

    public LiveData<Banner> getBannerById(long id) {
        MutableLiveData<Banner> liveData = new MutableLiveData<>();
        List<Banner> list = _banners.getValue();
        if (list != null) {
            Banner local = list.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
            if (local != null) {
                liveData.postValue(local);
                return liveData;
            }
        }

        apiService.obtenerBanner(id).enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                liveData.postValue(response.isSuccessful() && response.body() != null
                        ? BannerMapper.fromResponse(response.body())
                        : null);
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    public void createBanner(BannerRequest request, File imageFile) {
        try {
            RequestBody bannerJson = RequestBody.create(
                    BannerMapper.toJson(request),
                    MediaType.parse("application/json")
            );

            MultipartBody.Part imagePart = null;
            if (imageFile != null && imageFile.exists()) {
                RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/*"));
                imagePart = MultipartBody.Part.createFormData("imagen", imageFile.getName(), requestFile);
            }

            apiService.crearBanner(bannerJson, imagePart).enqueue(new Callback<BannerResponse>() {
                @Override
                public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Banner> updatedList = new ArrayList<>();
                        updatedList.add(BannerMapper.fromResponse(response.body()));
                        if (_banners.getValue() != null) updatedList.addAll(_banners.getValue());
                        _banners.postValue(updatedList);
                        Log.d(TAG, "✅ Banner creado: " + response.body().getTitle());
                    } else {
                        _errorMessage.postValue("Error al crear banner (" + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(Call<BannerResponse> call, Throwable t) {
                    _errorMessage.postValue("Error de red: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Excepción en createBanner", e);
            _errorMessage.postValue("Excepción: " + e.getMessage());
        }
    }

    public void updateBanner(long id, BannerRequest request, File imageFile) {
        try {
            RequestBody bannerJson = RequestBody.create(
                    BannerMapper.toJson(request),
                    MediaType.parse("application/json")
            );

            MultipartBody.Part imagePart = null;
            if (imageFile != null && imageFile.exists()) {
                RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/*"));
                imagePart = MultipartBody.Part.createFormData("imagen", imageFile.getName(), requestFile);
            }

            apiService.actualizarBanner(id, bannerJson, imagePart).enqueue(new Callback<BannerResponse>() {
                @Override
                public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Banner> current = _banners.getValue() != null ? _banners.getValue() : new ArrayList<>();
                        List<Banner> updatedList = new ArrayList<>();
                        for (Banner b : current) {
                            if (b.getId().equals(response.body().getId())) {
                                updatedList.add(BannerMapper.fromResponse(response.body()));
                            } else {
                                updatedList.add(b);
                            }
                        }
                        _banners.postValue(updatedList);
                        Log.d(TAG, "🟡 Banner actualizado: " + response.body().getTitle());
                    } else {
                        _errorMessage.postValue("Error al actualizar banner (" + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(Call<BannerResponse> call, Throwable t) {
                    _errorMessage.postValue("Error de red: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Excepción en updateBanner", e);
            _errorMessage.postValue("Excepción: " + e.getMessage());
        }
    }

    public void deleteBanner(long id) {
        apiService.eliminarBanner(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Banner> current = _banners.getValue() != null ? _banners.getValue() : new ArrayList<>();
                    List<Banner> updatedList = new ArrayList<>();
                    for (Banner b : current) {
                        if (!b.getId().equals(id)) updatedList.add(b);
                    }
                    _banners.postValue(updatedList);
                    Log.d(TAG, "🔴 Banner eliminado: " + id);
                } else {
                    _errorMessage.postValue("Error al eliminar banner (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _errorMessage.postValue("Error de red: " + t.getMessage());
            }
        });
    }

    // -----------------------------
    // Cleanup
    // -----------------------------
    public void shutdown() {
        disconnectWebSocket();
    }
}

package com.marlodev.app_android.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.dto.ApiResponse;
import com.marlodev.app_android.dto.tag.TagMapper;
import com.marlodev.app_android.dto.tag.TagRequest;
import com.marlodev.app_android.dto.tag.TagResponse;
import com.marlodev.app_android.dto.order.network.TagApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional y "a prueba de balas" para Tags.
 * Gestiona el CRUD completo vía REST API.
 * Asegura la consistencia del estado recargando la lista después de cada operación de escritura.
 * Convierte DTOs a modelos de Dominio usando TagMapper.
 */
public class TagRepository {

    private static final String TAG_LOG = "TagRepository";

    private final TagApiService apiService;

    private final MutableLiveData<List<Tag>> _tags = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _successMessage = new MutableLiveData<>();

    public final LiveData<List<Tag>> tags = _tags;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;
    public final LiveData<String> successMessage = _successMessage;

    public TagRepository(TagApiService apiService) {
        this.apiService = apiService;
    }

    // -----------------------------
    // REST API - CRUD COMPLETO
    // -----------------------------

    // -- Actualizar --
    public void createTag(String name) {
        _isLoading.postValue(true);
        TagRequest request = new TagRequest(name);
        apiService.createTag(request).enqueue(new Callback<ApiResponse<TagResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TagResponse>> call, Response<ApiResponse<TagResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG_LOG, "✅ Petición CREATE enviada. Recargando lista...");
                    _successMessage.postValue("Tag '" + response.body().getData().getName() + "' creado.");
                    loadTags(); // Estrategia "a prueba de balas": recargar la fuente de verdad.
                } else {
                    _isLoading.postValue(false);
                    _errorMessage.postValue("Error al crear el tag.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TagResponse>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de red al crear: " + t.getMessage());
            }
        });
    }

    // -- Actualizar --
    public void updateTag(Integer id, String newName) {
        _isLoading.postValue(true);
        TagRequest request = new TagRequest(newName);
        apiService.updateTag(id, request).enqueue(new Callback<ApiResponse<TagResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TagResponse>> call, Response<ApiResponse<TagResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG_LOG, "✅ Petición UPDATE enviada. Recargando lista...");
                     _successMessage.postValue("Tag actualizado a '" + response.body().getData().getName() + "'.");
                    loadTags(); // Recargar la fuente de verdad.
                } else {
                    _isLoading.postValue(false);
                    _errorMessage.postValue("Error al actualizar el tag.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TagResponse>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de red al actualizar: " + t.getMessage());
            }
        });
    }

    // --- Listar ---
    public void loadTags() {
        _isLoading.postValue(true);
        apiService.getTags().enqueue(new Callback<ApiResponse<List<TagResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TagResponse>>> call, Response<ApiResponse<List<TagResponse>>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TagResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Tag> domainTags = TagMapper.fromResponseList(apiResponse.getData());
                        _tags.postValue(domainTags);
                        Log.d(TAG_LOG, "✅ Tags cargados y mapeados: " + domainTags.size());
                    } else {
                        _errorMessage.postValue(apiResponse.getMessage());
                    }
                } else {
                    _errorMessage.postValue("Error al cargar tags (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TagResponse>>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de red: " + t.getMessage());
            }
        });
    }

    // --- Buscar por id ---
    public LiveData<Tag> getTagById(Integer id) {
        MutableLiveData<Tag> liveData = new MutableLiveData<>();
        List<Tag> list = _tags.getValue();
        if (list != null) {
            Tag local = list.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
            if (local != null) {
                liveData.postValue(local);
                return liveData;
            }
        }

        apiService.getTagById(id).enqueue(new Callback<ApiResponse<TagResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TagResponse>> call, Response<ApiResponse<TagResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Tag domainTag = TagMapper.fromResponse(response.body().getData());
                    liveData.postValue(domainTag);
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TagResponse>> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    // --- Eliminar ---
    public void deleteTag(Integer id) {
        _isLoading.postValue(true);
        apiService.deleteTag(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG_LOG, "✅ Petición DELETE enviada. Recargando lista...");
                    _successMessage.postValue("Tag eliminado con éxito.");
                    loadTags(); // Recargar la fuente de verdad.
                } else {
                    _isLoading.postValue(false);
                    _errorMessage.postValue("Error al eliminar el tag.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de red al eliminar: " + t.getMessage());
            }
        });
    }

    /**
     * Limpia el mensaje de éxito después de ser mostrado para evitar que se muestre de nuevo (ej. en rotaciones).
     */
     public void clearSuccessMessage() {
        _successMessage.postValue(null);
     }
}

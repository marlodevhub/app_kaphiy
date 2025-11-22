package com.marlodev.app_android.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.dto.ApiResponse;
import com.marlodev.app_android.dto.tag.TagMapper;
import com.marlodev.app_android.dto.tag.TagResponse;
import com.marlodev.app_android.network.TagApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional para Tags.
 * Gestiona REST API, convierte DTOs a modelos de Dominio y mantiene LiveData sincronizado.
 */
public class TagRepository {

    private static final String TAG_LOG = "TagRepository";

    private final TagApiService apiService;

    private final MutableLiveData<List<Tag>> _tags = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    public final LiveData<List<Tag>> tags = _tags;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;

    public TagRepository(TagApiService apiService) {
        this.apiService = apiService;
    }

    // -----------------------------
    // REST API
    // -----------------------------
    /**
     * Carga todos los tags desde el backend, realizando la conversión de DTO a Dominio.
     */
    public void loadTags() {
        _isLoading.postValue(true);
        // La llamada ahora espera una lista de DTOs (TagResponse)
        apiService.getTags().enqueue(new Callback<ApiResponse<List<TagResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TagResponse>>> call, Response<ApiResponse<List<TagResponse>>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TagResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // AQUÍ OCURRE LA MAGIA: El Mapper convierte la lista de DTOs a modelos de Dominio.
                        List<Tag> domainTags = TagMapper.fromResponseList(apiResponse.getData());
                        _tags.postValue(domainTags);
                        Log.d(TAG_LOG, "✅ Tags cargados y mapeados: " + domainTags.size());
                    } else {
                        _errorMessage.postValue(apiResponse.getMessage());
                        Log.e(TAG_LOG, "⚠️ Error al cargar tags: " + apiResponse.getMessage());
                    }
                } else {
                    _errorMessage.postValue("Error al cargar tags (" + response.code() + ")");
                    Log.e(TAG_LOG, "⚠️ Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TagResponse>>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de red: " + t.getMessage());
                Log.e(TAG_LOG, "❌ Falló la carga de tags", t);
            }
        });
    }

    /**
     * Obtiene un tag por ID, realizando la conversión de DTO a Dominio.
     * @param id ID del tag
     * @return LiveData<Tag> con el resultado o null si no existe
     */
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

        // La llamada ahora espera un DTO (TagResponse)
        apiService.getTagById(id).enqueue(new Callback<ApiResponse<TagResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TagResponse>> call, Response<ApiResponse<TagResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // El Mapper convierte el DTO a un modelo de Dominio.
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
}

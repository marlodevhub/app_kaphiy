package com.marlodev.app_android.network;

import com.marlodev.app_android.dto.ApiResponse;
import com.marlodev.app_android.dto.tag.TagResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Servicio de API para manejar Tags.
 * Define las llamadas HTTP hacia el backend.
 * Devuelve DTOs (Data Transfer Objects) para desacoplar la capa de red.
 */
public interface TagApiService {

    @GET("/api/admin/tags")
    Call<ApiResponse<List<TagResponse>>> getTags();

    @GET("/api/admin/tags/{id}")
    Call<ApiResponse<TagResponse>> getTagById(@Path("id") Integer id);
}

package com.marlodev.app_android.data.network.api;

import com.marlodev.app_android.data.network.model.ApiResponse;
import com.marlodev.app_android.data.network.model.tag.TagRequest;
import com.marlodev.app_android.data.network.model.tag.TagResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Servicio de API para manejar Tags.
 * Define el CRUD completo de llamadas HTTP hacia el backend.
 * Devuelve DTOs (Data Transfer Objects) para desacoplar la capa de red.
 */
public interface TagApiService {

    // --- READ ---
    @GET("/api/admin/tags")
    Call<ApiResponse<List<TagResponse>>> getTags();

    @GET("/api/admin/tags/{id}")
    Call<ApiResponse<TagResponse>> getTagById(@Path("id") Integer id);

    // --- CREATE ---
    @POST("/api/admin/tags")
    Call<ApiResponse<TagResponse>> createTag(@Body TagRequest tagRequest);

    // --- UPDATE ---
    @PUT("/api/admin/tags/{id}")
    Call<ApiResponse<TagResponse>> updateTag(@Path("id") Integer id, @Body TagRequest tagRequest);

    // --- DELETE ---
    @DELETE("/api/admin/tags/{id}")
    Call<ApiResponse<Void>> deleteTag(@Path("id") Integer id);
}

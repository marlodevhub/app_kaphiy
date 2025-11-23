package com.marlodev.app_android.dto.order.network;

import com.marlodev.app_android.dto.banner.BannerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * BannerApiService
 *
 * Servicio Retrofit para consumir el backend de Banners.
 * Profesional y listo para producción.
 */
public interface BannerApiService {

    // ───────────────────────────────
    // 🟢 CREAR BANNER
    // ───────────────────────────────
    @Multipart
    @POST("/admin/banners")
    Call<BannerResponse> crearBanner(
            @Part("banner") RequestBody bannerJson,      // BannerRequest convertido a JSON
            @Part MultipartBody.Part imagen             // Imagen como archivo
    );

    // ───────────────────────────────
    // 📄 LISTAR TODOS LOS BANNERS
    // ───────────────────────────────
    @GET("/api/admin/banners")
    Call<List<BannerResponse>> listarBanners();

    // ───────────────────────────────
    // 🔍 OBTENER UN BANNER POR ID
    // ───────────────────────────────
    @GET("/admin/banners/{id}")
    Call<BannerResponse> obtenerBanner(@Path("id") Long id);

    // ───────────────────────────────
    // ✏️ ACTUALIZAR BANNER
    // ───────────────────────────────
    @Multipart
    @PUT("/admin/banners/{id}")
    Call<BannerResponse> actualizarBanner(
            @Path("id") Long id,
            @Part("banner") RequestBody bannerJson,
            @Part MultipartBody.Part imagen    // Opcional
    );

    // ───────────────────────────────
    // ❌ ELIMINAR BANNER
    // ───────────────────────────────
    @DELETE("/admin/banners/{id}")
    Call<Void> eliminarBanner(@Path("id") Long id);
}
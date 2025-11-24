package com.marlodev.app_android.data.network.api;

import com.marlodev.app_android.data.network.model.product.ProductResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
public interface ProductApiService {

    // 🛍️ PRODUCTOS
    @GET("admin/products")
    Call<List<ProductResponse>> getProducts();

    @GET("admin/products/{id}")
    Call<ProductResponse> getProductById(@Path("id") long id);

    @Multipart
    @POST("admin/products")
    Call<ProductResponse> createProduct(
            @Part("producto") RequestBody productJson,
            @Part MultipartBody.Part[] imagenes
    );

    @Multipart
    @PUT("admin/products/{id}")
    Call<ProductResponse> updateProduct(
            @Path("id") long id,
            @Part("producto") RequestBody productJson,
            @Part MultipartBody.Part[] nuevasImagenes
    );

    @DELETE("admin/products/{id}")
    Call<Void> deleteProduct(@Path("id") long id);

    @DELETE("admin/products/{id}/imagen/{publicId}")
    Call<Void> deleteProductImage(@Path("id") long id, @Path("publicId") String publicId);
}
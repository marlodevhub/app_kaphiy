package com.marlodev.app_android.data.network.api;

import com.marlodev.app_android.data.network.model.auth.LoginRequest;
import com.marlodev.app_android.data.network.model.auth.LoginResponse;
import com.marlodev.app_android.data.network.model.auth.RegisterRequest;
import com.marlodev.app_android.data.network.model.auth.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    // 🔐 AUTENTICACIÓN
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Otros endpoints generales pueden ir aquí
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

}

package com.marlodev.app_android.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.model.auth.LoginRequest;
import com.marlodev.app_android.data.network.model.auth.LoginResponse;
import com.marlodev.app_android.data.network.model.auth.RegisterRequest;
import com.marlodev.app_android.data.network.model.auth.RegisterResponse;
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.network.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(Context context) {
        Retrofit retrofit = ApiClient.getClient(context);
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * Realiza login y devuelve MutableLiveData con LoginResponse (o null si error)
     */
    public MutableLiveData<LoginResponse> login(String username, String password) {
        MutableLiveData<LoginResponse> liveData = new MutableLiveData<>();
        LoginRequest req = new LoginRequest(username, password);
        apiService.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e("AuthRepository", "Login failed: code=" + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e("AuthRepository", "Login error: " + t.getMessage());
                liveData.postValue(null);
            }
        });
        return liveData;
    }
    public MutableLiveData<RegisterResponse> register(String name, String email, String password) {
        MutableLiveData<RegisterResponse> liveData = new MutableLiveData<>();

        RegisterRequest req = new RegisterRequest(name, email, password);

        apiService.register(req).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    RegisterResponse error = new RegisterResponse();
                    liveData.postValue(error);
                    Log.e("AuthRepository", "Register failed: code=" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                RegisterResponse error = new RegisterResponse();
                liveData.postValue(error);
                Log.e("AuthRepository", "Register error: " + t.getMessage());
            }
        });

        return liveData;
    }

}

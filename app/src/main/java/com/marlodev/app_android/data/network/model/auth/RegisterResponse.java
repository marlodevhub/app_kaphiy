package com.marlodev.app_android.data.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Método para validar si el registro fue exitoso
    public boolean isSuccess() {
        return accessToken != null && !accessToken.isEmpty();
    }
}

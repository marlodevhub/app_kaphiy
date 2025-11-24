package com.marlodev.app_android.data.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Clase genérica para mapear la respuesta del backend.
 * @param <T> Tipo de datos que contiene la respuesta (Tag, Product, etc.)
 */
public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    // Getters y setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
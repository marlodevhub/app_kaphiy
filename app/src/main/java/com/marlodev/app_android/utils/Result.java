package com.marlodev.app_android.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Wrapper genérico para manejar estados de UI de forma centralizada.
 * - Status.LOADING: cuando se está cargando información.
 * - Status.SUCCESS: cuando la operación fue exitosa y hay datos.
 * - Status.ERROR: cuando ocurrió un error.
 *
 * Se puede usar con LiveData, StateFlow, o cualquier patrón MVVM.
 */
public class Result<T> {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    // Constructor privado: usar los métodos estáticos para crear instancias
    private Result(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /** Crea un estado de carga */
    public static <T> Result<T> loading() {
        return new Result<>(Status.LOADING, null, null);
    }

    /** Crea un estado de éxito con datos */
    public static <T> Result<T> success(@NonNull T data) {
        return new Result<>(Status.SUCCESS, data, null);
    }

    /** Crea un estado de error con mensaje */
    public static <T> Result<T> error(@NonNull String message) {
        return new Result<>(Status.ERROR, null, message);
    }

    /** Crea un estado de error con datos opcionales */
    public static <T> Result<T> error(@NonNull String message, @Nullable T data) {
        return new Result<>(Status.ERROR, data, message);
    }

    // --------------------
    // Helpers para UI
    // --------------------

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}

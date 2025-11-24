package com.marlodev.app_android.domain;

public interface DomainCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}

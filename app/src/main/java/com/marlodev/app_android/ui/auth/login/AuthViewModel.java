package com.marlodev.app_android.ui.auth.login;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.data.network.model.auth.RegisterResponse;
import com.marlodev.app_android.data.repository.AuthRepository;
import com.marlodev.app_android.utils.JwtUtils;
import com.marlodev.app_android.utils.SessionManager;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<String> loginToken = new MutableLiveData<>();
    private final SessionManager sessionManager;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
        sessionManager = SessionManager.getInstance(application);
    }

    public LiveData<String> getLoginToken() { return loginToken; }

    public void login(String username, String password) {
        repository.login(username, password).observeForever(response -> {
            if (response != null && response.getAccessToken() != null) {
                String token = response.getAccessToken();
                // Guardar token en SharedPreferences (SessionManager)
                sessionManager.saveToken(token);

                // Extraer rol del token (si existe) y guardarlo
                try {
                    String role = JwtUtils.extractFirstRole(token);
                    if (role != null) sessionManager.saveRole(role);
                } catch (Exception e) {
                    Log.w("AuthViewModel", "No se pudo extraer role del token: " + e.getMessage());
                }

                loginToken.postValue(token);
                Log.d("AuthViewModel", "Login exitoso. Token guardado.");
            } else {
                loginToken.postValue(null);
                Log.e("AuthViewModel", "Login fallido (response null).");
            }
        });
    }

    private final MutableLiveData<RegisterResponse> registerStatus = new MutableLiveData<>();
    public LiveData<RegisterResponse> getRegisterStatus() { return registerStatus; }

    public void register(String nombre, String email, String password) {
        repository.register(nombre, email, password).observeForever(response -> {
            registerStatus.postValue(response);
        });
    }

    public void logout() {
        sessionManager.clear();
        loginToken.postValue(null);
    }
}

package com.marlodev.app_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;


public final class SessionManager {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();

        // Auto extraer rol desde JWT
        String role = JwtUtils.extractFirstRole(token);
        if (role != null) saveRole(role);
    }

    @Nullable
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveRole(String role) {
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "CLIENT");
    }

    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    @Nullable
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

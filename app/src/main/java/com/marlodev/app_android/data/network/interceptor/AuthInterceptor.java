package com.marlodev.app_android.data.network.interceptor;

import android.content.Context;
import androidx.annotation.NonNull;
import com.marlodev.app_android.utils.SessionManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    public AuthInterceptor(Context context) {
        this.sessionManager = SessionManager.getInstance(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String token = sessionManager.getToken();

        if (token == null) return chain.proceed(original);

        Request request = original.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(request);
    }
}

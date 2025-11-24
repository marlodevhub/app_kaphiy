package com.marlodev.app_android.di;

import android.content.Context;

import com.marlodev.app_android.BuildConfig;
import com.marlodev.app_android.data.network.websocket.dto.BannerWebSocketEvent;
import com.marlodev.app_android.data.network.websocket.dto.ProductWebSocketEvent;
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.network.api.BannerApiService;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.api.ProductApiService;
import com.marlodev.app_android.data.network.api.TagApiService;
import com.marlodev.app_android.data.repository.BannerRepositoryImpl;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.data.repository.TagRepositoryImpl;
import com.marlodev.app_android.utils.SessionManager;

import retrofit2.Retrofit;

/**
 * Contenedor de dependencias manual.
 * Sigue el patrón "Service Locator", una forma de Inyección de Dependencias manual.
 * Aquí se construyen y centralizan todas las dependencias importantes de la app.
 */
public class AppContainer {

    private final SessionManager sessionManager;
    private final Retrofit retrofit;

    // Repositorios (se crean una sola vez y se reutilizan)
    public final ProductRepositoryImpl productRepository;
    public final BannerRepositoryImpl bannerRepository;
    public final TagRepositoryImpl tagRepository;

    public AppContainer(Context context) {
        // Dependencias base
        this.sessionManager = SessionManager.getInstance(context.getApplicationContext());
        this.retrofit = ApiClient.getClient(context.getApplicationContext());

        // --- WebSockets ---
        // Se crea un WebSocketManager para cada tópico
        String token = sessionManager.getToken();
        GenericWebSocketManager<ProductWebSocketEvent> productWsManager = new GenericWebSocketManager<>(
                BuildConfig.WS_URL, token, "/topic/products", ProductWebSocketEvent.class
        );
        GenericWebSocketManager<BannerWebSocketEvent> bannerWsManager = new GenericWebSocketManager<>(
                BuildConfig.WS_URL, token, "/topic/banners", BannerWebSocketEvent.class
        );

        // --- Repositorios ---
        // Se construyen los repositorios con sus dependencias. Estos son los objetos que
        // el resto de la app consumirá. Son "singletons" en la práctica.
        this.productRepository = new ProductRepositoryImpl(
                retrofit.create(ProductApiService.class),
                productWsManager
        );
        this.bannerRepository = new BannerRepositoryImpl(
                retrofit.create(BannerApiService.class),
                bannerWsManager
        );
        this.tagRepository = new TagRepositoryImpl(retrofit.create(TagApiService.class));
    }
}

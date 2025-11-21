package com.marlodev.app_android.di;

import android.content.Context;

import com.marlodev.app_android.BuildConfig;
import com.marlodev.app_android.model.BannerWebSocketEvent;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.BannerApiService;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.TagApiService;
import com.marlodev.app_android.repository.BannerRepository;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.repository.TagRepository;
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
    public final ProductRepository productRepository;
    public final BannerRepository bannerRepository;
    public final TagRepository tagRepository;

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
        this.productRepository = new ProductRepository(
                retrofit.create(ProductApiService.class),
                productWsManager
        );
        this.bannerRepository = new BannerRepository(
                retrofit.create(BannerApiService.class),
                bannerWsManager
        );
        this.tagRepository = new TagRepository(retrofit.create(TagApiService.class));
    }
}

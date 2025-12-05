package com.marlodev.app_android.di;

import android.content.Context;

import com.marlodev.app_android.BuildConfig;
import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.api.ProductApiService;
import com.marlodev.app_android.data.network.api.TagApiService;
import com.marlodev.app_android.data.network.api.BannerApiService;
import com.marlodev.app_android.data.network.retrofit.ApiClient;

import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.websocket.events.BannerWebSocketEvent;
import com.marlodev.app_android.data.network.websocket.events.CartItemWebSocketEvent;
import com.marlodev.app_android.data.network.websocket.events.OrderWebSocketEvent;
import com.marlodev.app_android.data.network.websocket.events.ProductWebSocketEvent;

import com.marlodev.app_android.data.repository.*;

import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.domain.usecase.order.OrderUseCases;
import com.marlodev.app_android.domain.usecase.product.ProductUseCases;

import com.marlodev.app_android.ui.client.cart.ClientCartViewModelFactory;
import com.marlodev.app_android.ui.client.home.ClientHomeViewModelFactory;
import com.marlodev.app_android.ui.client.order.ClientOrderViewModelFactory;
import com.marlodev.app_android.ui.client.products.ProductDetailViewModelFactory;

import com.marlodev.app_android.utils.SessionManager;

import retrofit2.Retrofit;

/**
 * AppContainer profesional y escalable.
 * Punto central de inyección manual de dependencias.
 */
public class AppContainer {

    // ------------------------ DEPENDENCIAS BASE ------------------------
    private final Context appContext;
    private final SessionManager sessionManager;
    private final Retrofit retrofit;

    // ------------------------ REPOSITORIOS ------------------------
    public final BannerRepositoryImpl bannerRepository;
    public final TagRepositoryImpl tagRepository;
    public final ProductRepositoryImpl productRepository;
    public final CartRepositoryImpl cartRepository;
    public final OrderRepositoryImpl orderRepository;

    // ------------------------ USE CASES ------------------------
    public final ProductUseCases productUseCases;
    public final CartUseCases cartUseCases;
    public final OrderUseCases orderUseCases;

    // ------------------------ VIEWMODEL FACTORIES ------------------------
    public final ProductDetailViewModelFactory productDetailViewModelFactory;;
    public final ClientHomeViewModelFactory clientHomeViewModelFactory;
    public final ClientCartViewModelFactory clientCartViewModelFactory;
    public final ClientOrderViewModelFactory clientOrderViewModelFactory;


    public AppContainer(Context context) {

        // ------------------------ BASE ------------------------
        this.appContext = context.getApplicationContext();
        this.sessionManager = SessionManager.getInstance(appContext);
        this.retrofit = ApiClient.getClient(appContext);

        String token = sessionManager.getToken();

        // ------------------------ WEBSOCKETS ------------------------
        GenericWebSocketManager<BannerWebSocketEvent> bannerWs =
                new GenericWebSocketManager<>(BuildConfig.WS_URL, token, "/topic/banners", BannerWebSocketEvent.class);

        GenericWebSocketManager<ProductWebSocketEvent> productWs =
                new GenericWebSocketManager<>(BuildConfig.WS_URL, token, "/topic/products", ProductWebSocketEvent.class);

        GenericWebSocketManager<CartItemWebSocketEvent> cartWs =
                new GenericWebSocketManager<>(BuildConfig.WS_URL, token, "/topic/cart", CartItemWebSocketEvent.class);

        GenericWebSocketManager<OrderWebSocketEvent> orderWs =
                new GenericWebSocketManager<>(BuildConfig.WS_URL, token, "/topic/orders", OrderWebSocketEvent.class);


        // ------------------------ REPOSITORIOS ------------------------
        this.tagRepository = new TagRepositoryImpl(retrofit.create(TagApiService.class));

        this.bannerRepository = new BannerRepositoryImpl(
                retrofit.create(BannerApiService.class), bannerWs
        );

        this.productRepository = new ProductRepositoryImpl(
                retrofit.create(ProductApiService.class), productWs
        );

        this.cartRepository = new CartRepositoryImpl(
                retrofit.create(CartApi.class), cartWs
        );

        this.orderRepository = new OrderRepositoryImpl(
                retrofit.create(OrderApi.class), orderWs
        );

        // ------------------------ USE CASES (MÓDULOS) ------------------------
        this.productUseCases = ProductModule.provideProductUseCases(productRepository);
        this.cartUseCases = CartModule.provideCartUseCases(cartRepository);
        this.orderUseCases = OrderModule.provideOrderUseCases(orderRepository);

        // ------------------------ VIEWMODELS ------------------------
        this.productDetailViewModelFactory = new ProductDetailViewModelFactory(productUseCases);
        this.clientCartViewModelFactory = new ClientCartViewModelFactory(cartUseCases);
        this.clientOrderViewModelFactory = new ClientOrderViewModelFactory(orderUseCases);

        this.clientHomeViewModelFactory = new ClientHomeViewModelFactory(
                productRepository,
                tagRepository,
                bannerRepository
        );
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}

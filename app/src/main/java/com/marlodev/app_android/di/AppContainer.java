package com.marlodev.app_android.di;

import android.content.Context;

import com.marlodev.app_android.BuildConfig;
import com.marlodev.app_android.data.network.api.CartApi;
import com.marlodev.app_android.data.network.api.OrderApi;
import com.marlodev.app_android.data.network.websocket.dto.BannerWebSocketEvent;
import com.marlodev.app_android.data.network.websocket.dto.ProductWebSocketEvent;
import com.marlodev.app_android.data.network.retrofit.ApiClient;
import com.marlodev.app_android.data.network.api.BannerApiService;
import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.network.api.ProductApiService;
import com.marlodev.app_android.data.network.api.TagApiService;
import com.marlodev.app_android.data.repository.BannerRepositoryImpl;
import com.marlodev.app_android.data.repository.CartRepositoryImpl;
import com.marlodev.app_android.data.repository.OrderRepositoryImpl;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.data.repository.TagRepositoryImpl;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.domain.usecase.cart.CheckoutUseCase;
import com.marlodev.app_android.domain.usecase.order.cliente.OrderUseCases;
import com.marlodev.app_android.domain.usecase.product.GetProductByIdUseCase;
import com.marlodev.app_android.domain.usecase.product.ProductUseCases;
import com.marlodev.app_android.ui.client.cart.ClientCartViewModelFactory;
import com.marlodev.app_android.ui.client.order.ClientOrderViewModelFactory;
import com.marlodev.app_android.ui.client.products.ProductDetailViewModelFactory;
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

    // --- Repositorios ---
    public final BannerRepositoryImpl bannerRepository;
    public final TagRepositoryImpl tagRepository;
    public final ProductRepositoryImpl productRepository;
    public final CartRepositoryImpl cartRepository;
    public final OrderRepositoryImpl orderRepository;

    // --- Casos de uso ---
    public final ProductUseCases productUseCases;
    public final CartUseCases cartUseCases;
    public final OrderUseCases orderUseCases;
    public final CheckoutUseCase checkoutUseCase;

    // --- ViewModel Factories ---
    public final ProductDetailViewModelFactory productDetailViewModelFactory;
    public final ClientCartViewModelFactory clientCartViewModelFactory;
    public final ClientOrderViewModelFactory clientOrderViewModelFactory;

    public AppContainer(Context context) {
        // --- Dependencias base ---
        this.sessionManager = SessionManager.getInstance(context.getApplicationContext());
        this.retrofit = ApiClient.getClient(context.getApplicationContext());

        // --- WebSockets ---
        String token = sessionManager.getToken();
        GenericWebSocketManager<ProductWebSocketEvent> productWsManager = new GenericWebSocketManager<>(
                BuildConfig.WS_URL, token, "/topic/products", ProductWebSocketEvent.class
        );
        GenericWebSocketManager<BannerWebSocketEvent> bannerWsManager = new GenericWebSocketManager<>(
                BuildConfig.WS_URL, token, "/topic/banners", BannerWebSocketEvent.class
        );

        // --- Repositorios ---
        this.bannerRepository = new BannerRepositoryImpl(
                retrofit.create(BannerApiService.class),
                bannerWsManager
        );
        this.tagRepository = new TagRepositoryImpl(
                retrofit.create(TagApiService.class)
        );
        this.productRepository = new ProductRepositoryImpl(
                retrofit.create(ProductApiService.class),
                productWsManager
        );
        this.cartRepository = new CartRepositoryImpl(
                retrofit.create(CartApi.class));
        this.orderRepository = new OrderRepositoryImpl(
                retrofit.create(OrderApi.class));



        // --- Casos de uso ---
        this.productUseCases = ProductModule.provideProductUseCases(productRepository);
        this.cartUseCases = CartModule.provideCartUseCases(cartRepository);
        this.orderUseCases = OrderModule.provideCartUseCases(orderRepository);
        this.checkoutUseCase = cartUseCases.getCheckoutUseCase();

        // --- ViewModel Factories ---
        this.productDetailViewModelFactory = new ProductDetailViewModelFactory(productUseCases);
        this.clientCartViewModelFactory = new ClientCartViewModelFactory(cartUseCases);
        this.clientOrderViewModelFactory = new ClientOrderViewModelFactory(orderUseCases);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

}

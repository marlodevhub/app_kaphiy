package com.marlodev.app_android.ui.client.home;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.network.websocket.GenericWebSocketManager;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.data.repository.TagRepositoryImpl;
import com.marlodev.app_android.data.repository.BannerRepositoryImpl;
import com.marlodev.app_android.domain.model.Banner;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.model.Tag;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ViewModel profesional para ClientHomeFragment.
 * El ProductRepository es la única fuente de la verdad para los productos,
 * combinando REST y WebSocket internamente.
 */
public class ClientHomeViewModel extends ViewModel {

    private static final long MIN_SKELETON_DISPLAY_TIME = 800L; // ms

    // El ViewModel solo depende de los repositorios, no de los detalles de implementación (como WebSockets)
    private final ProductRepositoryImpl productRepository;
    private final TagRepositoryImpl tagRepository;
    private final BannerRepositoryImpl bannerRepository;

    private final MediatorLiveData<List<Product>> products = new MediatorLiveData<>();
    private final MediatorLiveData<List<Tag>> tags = new MediatorLiveData<>();
    private final MediatorLiveData<List<Banner>> banners = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorMessage = new MediatorLiveData<>();

    public ClientHomeViewModel(
            @NonNull ProductRepositoryImpl productRepository,
            @NonNull TagRepositoryImpl tagRepository,
            @NonNull BannerRepositoryImpl bannerRepository
    ) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.bannerRepository = bannerRepository;

        loadInitialData();
    }

    private void loadInitialData() {
        // Productos: El ViewModel simplemente observa el LiveData del repositorio.
        // El repositorio se encarga de la carga inicial y las actualizaciones por WebSocket.
        loadDataWithSkeleton(
                products,
                productRepository.products, // Se observa el LiveData público del repositorio
                this::createProductSkeletonList,
                productRepository::loadProducts // La función de carga es la del propio repositorio
        );

        // Tags
        loadDataWithSkeleton(
                tags,
                tagRepository.tags,
                this::createTagSkeletonList,
                tagRepository::loadTags
        );

        // Banners
        loadDataWithSkeleton(
                banners,
                bannerRepository.banners,
                this::createBannerSkeletonList,
                bannerRepository::loadBanners
        );

        // Centraliza errores
        errorMessage.addSource(productRepository.errorMessage, errorMessage::setValue);
        errorMessage.addSource(tagRepository.errorMessage, errorMessage::setValue);
        errorMessage.addSource(bannerRepository.errorMessage, errorMessage::setValue);
    }

    private <T> void loadDataWithSkeleton(
            @NonNull MediatorLiveData<List<T>> uiLiveData,
            @NonNull LiveData<List<T>> repoLiveData,
            @NonNull SkeletonProvider<T> skeletonProvider,
            @NonNull LoadFunction loadFunction
    ) {
        long startTime = System.currentTimeMillis();
        uiLiveData.setValue(skeletonProvider.create(5));

        AtomicBoolean isInitialDataArrived = new AtomicBoolean(false);

        uiLiveData.addSource(repoLiveData, realData -> {
            if (realData == null) return;

            if (isInitialDataArrived.compareAndSet(false, true)) {
                if (realData.isEmpty()) return;
                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = MIN_SKELETON_DISPLAY_TIME - elapsedTime;
                if (remainingTime > 0) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> uiLiveData.setValue(realData), remainingTime);
                } else {
                    uiLiveData.setValue(realData);
                }
            } else {
                uiLiveData.setValue(realData);
            }
        });

        loadFunction.load();
    }

    // El ViewModel delega el control del ciclo de vida del WebSocket al repositorio
    public void startWebSocket() {
        productRepository.connectWebSocket();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // El repositorio es responsable de limpiar sus propias conexiones
        productRepository.shutdown();
    }

    // Skeletons
    private List<Product> createProductSkeletonList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Product.builder().isSkeleton(true).id((long) -i).build())
                .collect(Collectors.toList());
    }

    private List<Tag> createTagSkeletonList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Tag.builder().isSkeleton(true).id(-i).build())
                .collect(Collectors.toList());
    }

    private List<Banner> createBannerSkeletonList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Banner.builder().isSkeleton(true).id((long) -i).build())
                .collect(Collectors.toList());
    }

    // Getters
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Tag>> getTags() { return tags; }
    public LiveData<List<Banner>> getBanners() { return banners; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // LiveData del estado del WS para la UI
    public LiveData<GenericWebSocketManager.ConnectionState> getWebSocketState() {
        return productRepository.getWebSocketState();
    }



    @FunctionalInterface interface SkeletonProvider<T> { List<T> create(int count); }
    @FunctionalInterface interface LoadFunction { void load(); }
}

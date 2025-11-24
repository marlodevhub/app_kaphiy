package com.marlodev.app_android.ui.client.home;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.data.repository.TagRepositoryImpl;
import com.marlodev.app_android.domain.model.Banner;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.model.Tag;
import com.marlodev.app_android.data.repository.BannerRepositoryImpl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ViewModel profesional para ClientHomeFragment.
 * Implementa una arquitectura de carga de élite:
 * - Garantiza un tiempo mínimo de visualización del esqueleto para evitar parpadeos (flashes).
 * - Orquesta el estado de la UI de forma reactiva y robusta.
 */
public class ClientHomeViewModel extends ViewModel {
    private static final String TAG_LOG = "ClientHomeViewModel";
    private static final long MIN_SKELETON_DISPLAY_TIME = 800L; // 800 ms

    private final ProductRepositoryImpl productRepository;
    private final TagRepositoryImpl tagRepository;
    private final BannerRepositoryImpl bannerRepository;

    // LiveData públicos que la UI observará.
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

    public void loadInitialData() {
        loadDataWithSkeleton(
                products,
                productRepository.products,
                this::createProductSkeletonList,
                productRepository::loadProducts
        );
        loadDataWithSkeleton(
                tags,
                tagRepository.tags,
                this::createTagSkeletonList,
                tagRepository::loadTags
        );
        loadDataWithSkeleton(
                banners,
                bannerRepository.banners,
                this::createBannerSkeletonList,
                bannerRepository::loadBanners
        );

        // Centralizar errores
        errorMessage.addSource(productRepository.errorMessage, error -> { if (error != null) errorMessage.setValue(error); });
        errorMessage.addSource(tagRepository.errorMessage, error -> { if (error != null) errorMessage.setValue(error); });
        errorMessage.addSource(bannerRepository.errorMessage, error -> { if (error != null) errorMessage.setValue(error); });
    }

    private <T> void loadDataWithSkeleton(
            @NonNull MediatorLiveData<List<T>> uiLiveData,
            @NonNull LiveData<List<T>> repoLiveData,
            @NonNull SkeletonProvider<T> skeletonProvider,
            @NonNull LoadFunction loadFunction
    ) {
        long startTime = System.currentTimeMillis();
        uiLiveData.setValue(skeletonProvider.create(5)); // 1. Emitir esqueletos inmediatamente

        AtomicBoolean isInitialDataArrived = new AtomicBoolean(false);

        uiLiveData.addSource(repoLiveData, realData -> {
            if (realData == null) {
                return;
            }

            // Para la carga inicial, se aplica la lógica del esqueleto.
            if (isInitialDataArrived.compareAndSet(false, true)) {
                // Ignorar la primera emisión si es vacía para evitar que el esqueleto desaparezca.
                if (realData.isEmpty()) {
                    return;
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = MIN_SKELETON_DISPLAY_TIME - elapsedTime;

                if (remainingTime > 0) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> uiLiveData.setValue(realData), remainingTime);
                } else {
                    uiLiveData.setValue(realData);
                }
            } else {
                // Para actualizaciones subsecuentes (desde WS), se actualiza la UI directamente.
                uiLiveData.setValue(realData);
            }
        });

        loadFunction.load();
    }


    @FunctionalInterface
    interface SkeletonProvider<T> { List<T> create(int count); }
    @FunctionalInterface
    interface LoadFunction { void load(); }


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

    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Tag>> getTags() { return tags; }
    public LiveData<List<Banner>> getBanners() { return banners; }
    public LiveData<String> getErrorMessage() { return errorMessage; }


    public void startWebSocket() {
        productRepository.connectWebSocket();
        bannerRepository.connectWebSocket();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG_LOG, "🧹 Limpiando recursos WebSocket y repositorios");
        productRepository.shutdown();
        bannerRepository.shutdown();
    }
}

package com.marlodev.app_android.ui.client.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.data.network.websocket.ProductWebSocketService;
import com.marlodev.app_android.data.repository.BannerRepositoryImpl;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.data.repository.TagRepositoryImpl;

/**
 * Factory profesional para crear instancias de ClientHomeViewModel.
 * - Proporciona las dependencias (repositorios) necesarias.
 * - Desacopla la creación del ViewModel de la UI (Fragment/Activity).
 */
public class ClientHomeViewModelFactory implements ViewModelProvider.Factory {

    private final ProductRepositoryImpl productRepository;
    private final ProductWebSocketService productWebSocketService;
    private final TagRepositoryImpl tagRepository;
    private final BannerRepositoryImpl bannerRepository;

    public ClientHomeViewModelFactory(
        ProductRepositoryImpl productRepository,
        ProductWebSocketService productWebSocketService,
        TagRepositoryImpl tagRepository,
        BannerRepositoryImpl bannerRepository
    ) {
        this.productRepository = productRepository;
        this.productWebSocketService = productWebSocketService;
        this.tagRepository = tagRepository;
        this.bannerRepository = bannerRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientHomeViewModel.class)) {
            // Si la clase ViewModel solicitada es ClientHomeViewModel, crea una instancia con los repositorios.
            return (T) new ClientHomeViewModel(productRepository, productWebSocketService, tagRepository, bannerRepository);
        }
        // Si no es la clase esperada, lanza una excepción.
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

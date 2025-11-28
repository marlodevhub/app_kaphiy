package com.marlodev.app_android.ui.client.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.data.repository.BannerRepositoryImpl;
import com.marlodev.app_android.data.repository.ProductRepositoryImpl;
import com.marlodev.app_android.data.repository.TagRepositoryImpl;

/**
 * Factory profesional para crear instancias de ClientHomeViewModel.
 */
public class ClientHomeViewModelFactory implements ViewModelProvider.Factory {

    private final ProductRepositoryImpl productRepository;
    private final TagRepositoryImpl tagRepository;
    private final BannerRepositoryImpl bannerRepository;

    public ClientHomeViewModelFactory(
            ProductRepositoryImpl productRepository,
            TagRepositoryImpl tagRepository,
            BannerRepositoryImpl bannerRepository
    ) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.bannerRepository = bannerRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientHomeViewModel.class)) {
            return (T) new ClientHomeViewModel(productRepository, tagRepository, bannerRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

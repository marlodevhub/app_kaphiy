package com.marlodev.app_android.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.marlodev.app_android.MainApplication;
import com.marlodev.app_android.R;
import com.marlodev.app_android.adapter.client.BannerAdapter;
import com.marlodev.app_android.adapter.client.PopularAdapter;
import com.marlodev.app_android.adapter.client.TagAdapter;
import com.marlodev.app_android.databinding.FragmentClientHomeBinding;
import com.marlodev.app_android.di.AppContainer;
import com.marlodev.app_android.domain.Banner;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.ui.home.customer.ClientDetailActivity;
import com.marlodev.app_android.viewmodel.ClientHomeVM;
import com.marlodev.app_android.viewmodel.ClientHomeVMFactory;

import java.util.ArrayList;
import java.util.List;

public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ClientHomeVM clientHomeVM;
    private PopularAdapter popularAdapter;
    private TagAdapter tagAdapter;
    private BannerAdapter bannerAdapter;
    private boolean isInitialBannerLoad = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        setupAdapters();
        observeViewModel();

        clientHomeVM.startWebSocket();
    }

    private void initViewModel() {
        // 1. Obtener el contenedor de dependencias desde la clase Application
        AppContainer appContainer = ((MainApplication) requireActivity().getApplication()).appContainer;

        // 2. Usar los repositorios ya creados del contenedor para construir la Factory
        ClientHomeVMFactory factory = new ClientHomeVMFactory(
                appContainer.productRepository,
                appContainer.tagRepository,
                appContainer.bannerRepository
        );

        // 3. Crear el ViewModel. El Fragment ya no construye nada, solo pide las piezas.
        clientHomeVM = new ViewModelProvider(requireActivity(), factory).get(ClientHomeVM.class);
    }

    private void setupAdapters() {
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularView.setAdapter(popularAdapter);
        popularAdapter.setOnProductClickListener(this::openProductDetail);

        tagAdapter = new TagAdapter();
        binding.tagRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.tagRecyclerView.setAdapter(tagAdapter);

        bannerAdapter = new BannerAdapter(requireContext(), binding.bannerViewPager);
        binding.bannerViewPager.setAdapter(bannerAdapter);

        new TabLayoutMediator(binding.bannerTabLayout, binding.bannerViewPager, (tab, position) -> {
            tab.setCustomView(R.layout.tab_custom_dot);
        }).attach();

        bannerAdapter.setOnBannerClickListener(banner -> {
            if (!banner.isSkeleton()) {
                Snackbar.make(binding.getRoot(), "Banner: " + banner.getTitle(), Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.bannerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    View dot = tab.getCustomView().findViewById(R.id.dotView);
                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(30);
                    dot.setLayoutParams(params);
                    dot.setSelected(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    View dot = tab.getCustomView().findViewById(R.id.dotView);
                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(9);
                    dot.setLayoutParams(params);
                    dot.setSelected(false);
                }
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeViewModel() {
        clientHomeVM.getProducts().observe(getViewLifecycleOwner(), popularAdapter::submitList);
        clientHomeVM.getTags().observe(getViewLifecycleOwner(), tagAdapter::submitList);
        clientHomeVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        clientHomeVM.getBanners().observe(getViewLifecycleOwner(), banners -> {
            // La lista ahora se envía al ListAdapter, que calculará las diferencias y animará los cambios.
            bannerAdapter.submitList(banners);

            // La lógica para evitar el reinicio del slider se mantiene igual y es crucial.
            if (isInitialBannerLoad && binding.bannerTabLayout.getTabCount() > 0) {
                binding.bannerTabLayout.selectTab(binding.bannerTabLayout.getTabAt(0));
                isInitialBannerLoad = false;
            }
        });
    }

    private void showError(String error) {
        if (error != null && !error.isBlank()) {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void openProductDetail(Product product) {
        if (product.isSkeleton()) return;
        Intent intent = new Intent(requireContext(), ClientDetailActivity.class);
        intent.putExtra("productId", product.getId());
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

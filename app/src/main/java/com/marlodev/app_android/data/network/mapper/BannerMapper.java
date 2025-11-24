package com.marlodev.app_android.data.network.mapper;

import com.marlodev.app_android.data.network.model.banner.BannerRequest;
import com.marlodev.app_android.data.network.model.banner.BannerResponse;
import com.marlodev.app_android.domain.model.Banner;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BannerMapper {

    private final Gson gson = new Gson();

    // -----------------------------
    // Conversiones individuales
    // -----------------------------

    public Banner fromRequest(BannerRequest request) {
        if (request == null) return null;
        return Banner.builder()
                .title(request.getTitle())
                .order(request.getOrder())
                .build();
    }

    public BannerResponse toResponse(Banner banner) {
        if (banner == null) return null;
        return new BannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getOrder(),
                banner.getUrl(),
                banner.getPublicId()
        );
    }

    public Banner fromResponse(BannerResponse response) {
        if (response == null) return null;
        return Banner.builder()
                .id(response.getId())
                .title(response.getTitle())
                .order(response.getOrder())
                .url(response.getUrl())
                .publicId(response.getPublicId())
                .build();
    }

    // -----------------------------
    // Conversiones listas
    // -----------------------------

    public List<Banner> fromResponseList(List<BannerResponse> responses) {
        List<Banner> list = new ArrayList<>();
        if (responses != null) {
            for (BannerResponse r : responses) list.add(fromResponse(r));
        }
        return list;
    }

    public List<BannerResponse> toResponseList(List<Banner> banners) {
        List<BannerResponse> list = new ArrayList<>();
        if (banners != null) {
            for (Banner b : banners) list.add(toResponse(b));
        }
        return list;
    }

    // -----------------------------
    // Utilidad JSON para Retrofit
    // -----------------------------

    public String toJson(BannerRequest request) {
        return gson.toJson(request);
    }
}

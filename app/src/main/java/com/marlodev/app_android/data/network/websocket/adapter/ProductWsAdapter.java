package com.marlodev.app_android.data.network.websocket.adapter;
import com.marlodev.app_android.data.network.websocket.dto.ProductWebSocketEvent;
import com.marlodev.app_android.domain.model.Product;

public class ProductWsAdapter {

    // Crear un Product desde evento WS
    public static Product fromEvent(ProductWebSocketEvent event) {
        if (event == null) return null;
        Product product = new Product();
        updateFromEvent(product, event);
        return product;
    }

    // Actualizar un Product existente desde evento WS
    public static void updateFromEvent(Product product, ProductWebSocketEvent event) {
        if (product == null || event == null) return;

        if (event.getId() != null) product.setId(event.getId());
        if (event.getName() != null) product.setName(event.getName());
        if (event.getPrice() != null) product.setPrice(event.getPrice());
        if (event.getOldPrice() != null) product.setOldPrice(event.getOldPrice());
        if (event.getIsNew() != null) product.setIsNew(event.getIsNew());
        if (event.getRating() != null) product.setRating(event.getRating());
        if (event.getReviewsCount() != null) product.setReviewsCount(event.getReviewsCount());

        if (event.getImageUrl() != null && !event.getImageUrl().trim().isEmpty()
                && !product.getImageUrls().contains(event.getImageUrl())) {
            product.getImageUrls().add(event.getImageUrl());
        }
    }
}
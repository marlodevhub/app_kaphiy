package com.marlodev.app_android.data.network.websocket.adapter;

import java.util.ArrayList;
import java.util.List;

import com.marlodev.app_android.data.network.websocket.events.CartItemWebSocketEvent;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Extra;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.model.ProductVariant;

public class CartItemWsAdapter {

    // Crear un CartItem desde evento WS
    public static CartItem fromEvent(CartItemWebSocketEvent event) {
        if (event == null) return null;
        CartItem cartItem = new CartItem();
        updateFromEvent(cartItem, event);
        return cartItem;
    }

    // Actualizar un CartItem existente desde evento WS
    public static void updateFromEvent(CartItem cartItem, CartItemWebSocketEvent event) {
        if (cartItem == null || event == null) return;

        cartItem.setId(event.getId());
        cartItem.setQuantity(event.getQuantity());
        cartItem.setUnitPrice(event.getUnitPrice());
        cartItem.setTotalPrice(event.getTotalPrice());

//        // Producto
//        if (event.getProduct() != null) {
//            Product p = event.getProduct(); // ya es tu modelo de dominio Product
//            Product product = new Product();
//            product.setId(p.getId());
//            product.setName(p.getName());
//            product.setDescription(p.getDescription());
//            product.setPrice(p.getPrice());
//            product.setOldPrice(p.getOldPrice());
//            product.setDiscountPercent(p.getDiscountPercent());
//            product.setIsNew(p.getIsNew());
//            product.setRating(p.getRating());
//            product.setReviewsCount(p.getReviewsCount());
//            product.setStoreId(p.getStoreId());
//            product.setCategoryId(p.getCategoryId());
//            product.setImageUrls(p.getImageUrls());
//            product.setImagePublicIds(p.getImagePublicIds());
//            product.setVariants(p.getVariants()); // si quieres mantener referencias
//            product.setExtras(p.getExtras());
//            cartItem.setProduct(product);
//        }
//
//        // Variante seleccionada
//        if (event.getVariant() != null) {
//            ProductVariant v = event.getVariant(); // ya es tu modelo de dominio
//            ProductVariant variant = new ProductVariant();
//            variant.setId(v.getId());
//            variant.setName(v.getName());
//            variant.setPrice(v.getPrice());
//            variant.setStock(v.getStock());
//            cartItem.setVariant(variant);
//        }
//
//        // Extras seleccionados
//        if (event.getExtras() != null) {
//            List<Extra> extras = new ArrayList<>();
//            for (Extra e : event.getExtras()) {
//                Extra ex = new Extra();
//                ex.setId(e.getId());
//                ex.setName(e.getName());
//                ex.setPrice(e.getPrice());
//                extras.add(ex);
//            }
//            cartItem.setExtras(extras);
//        }
    }
}

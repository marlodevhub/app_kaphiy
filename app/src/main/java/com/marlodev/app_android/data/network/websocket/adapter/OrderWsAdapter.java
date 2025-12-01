package com.marlodev.app_android.data.network.websocket.adapter;

import com.marlodev.app_android.data.network.websocket.events.OrderWebSocketEvent;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderWsAdapter {
    // Crear un Order desde evento WS
    public static Order fromEvent(OrderWebSocketEvent event) {
        if (event == null) return null;
        Order order = new Order();
        updateFromEvent(order, event);
        return order;
    }

    // Actualizar un Order existente desde evento WS
    public static void updateFromEvent(Order order, OrderWebSocketEvent event) {
        if (order == null || event == null) return;

        var e = event.getOrder();

        // Datos principales
        order.setId(e.getId());
        order.setUserId(e.getUserId());
        order.setUsername(e.getUsername());
        order.setStatus(e.getStatus());
        order.setMessage(e.getMessage());
        order.setTotalAmount(e.getTotalAmount());

        // Fechas
        order.setCreatedAt(e.getCreatedAt());
        order.setUpdatedAt(e.getUpdatedAt());
        order.setConfirmedAt(e.getConfirmedAt());
        order.setPreparedAt(e.getPreparedAt());
        order.setReadyAt(e.getReadyAt());
        order.setDeliveredAt(e.getDeliveredAt());

        // Dirección
        order.setDeliveryAddress(e.getDeliveryAddress());
        order.setDeliveryLat(e.getDeliveryLat());
        order.setDeliveryLng(e.getDeliveryLng());

        // Store
        order.setStoreId(e.getStoreId());
        order.setStoreName(e.getStoreName());

        // Roles
        order.setBaristaId(e.getBaristaId());
        order.setDeliveryId(e.getDeliveryId());

        // Mapear items correctamente
        List<CartItem> items = new ArrayList<>();
        if (e.getItems() != null) {
            for (var itemResponse : e.getItems()) {
                CartItem item = new CartItem();
                item.setId(itemResponse.getId());
                item.setQuantity(itemResponse.getQuantity());
                item.setUnitPrice(itemResponse.getUnitPrice());
                item.setTotalPrice(itemResponse.getTotalPrice());

                // Mapear producto
                if (itemResponse.getProduct() != null) {
                    Product product = new Product();
                    product.setId(itemResponse.getProduct().getId());
                    product.setName(itemResponse.getProduct().getName());
                    product.setImageUrls(itemResponse.getProduct().getImageUrls() != null
                            ? itemResponse.getProduct().getImageUrls()
                            : new ArrayList<>());
                    item.setProduct(product);
                }

                items.add(item);
            }
        }
        order.setItems(items);
    }
}

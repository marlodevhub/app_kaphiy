// OrderMapper.java
package com.marlodev.app_android.domain.mappers_parceables;

import com.marlodev.app_android.domain.dtoParcelable.CartItemParcelable;
import com.marlodev.app_android.domain.dtoParcelable.OrderParcelable;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.OrderStatus;
import com.marlodev.app_android.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    // Domain → Parcelable
    public static OrderParcelable toParcelable(Order order) {
        OrderParcelable dto = new OrderParcelable();
        dto.id = order.getId();
        dto.status = order.getStatus() != null ? order.getStatus().name() : null;
        dto.message = order.getMessage();

        if (order.getItems() != null) {
            dto.items = new ArrayList<>();
            for (CartItem item : order.getItems()) {
                CartItemParcelable ci = new CartItemParcelable();
                ci.id = item.getId();
                ci.productName = item.getProduct() != null ? item.getProduct().getName() : null;
                ci.quantity = item.getQuantity();
                ci.unitPrice = item.getUnitPrice() != null ? item.getUnitPrice().toString() : null;
                ci.imageUrls = item.getProduct() != null && item.getProduct().getImageUrls() != null
                        ? new ArrayList<>(item.getProduct().getImageUrls())
                        : new ArrayList<>();
                dto.items.add(ci);
            }
        }

        return dto;
    }

    // Parcelable → Domain
    public static Order fromParcelable(OrderParcelable dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.id);
        order.setStatus(dto.status != null ? OrderStatus.valueOf(dto.status) : null);
        order.setMessage(dto.message);

        if (dto.items != null) {
            List<CartItem> items = new ArrayList<>();
            for (CartItemParcelable ci : dto.items) {
                CartItem item = new CartItem();
                item.setId(ci.id);

                // Crear objeto Product con nombre e imágenes
                if (ci.productName != null) {
                    Product product = Product.builder()
                            .id(null) // o algún valor válido si lo tienes
                            .name(ci.productName)
                            .imageUrls(ci.imageUrls != null ? ci.imageUrls : new ArrayList<>())
                            .build();
                    item.setProduct(product);
                }

                item.setQuantity(ci.quantity);
                item.setUnitPrice(ci.unitPrice != null ? new java.math.BigDecimal(ci.unitPrice) : null);
                items.add(item);
            }
            order.setItems(items);
        }

        return order;
    }
}

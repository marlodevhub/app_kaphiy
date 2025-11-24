package com.marlodev.app_android.domain.usecase;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.data.network.model.order.CartItem;
import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.utils.Result;
import java.util.List;

public class AddItemToCartUseCase {
    private final CartRepository cartRepository;

    public AddItemToCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public LiveData<Result<OrderResponse>> invoke(long productId, int quantity, List<CartItem> currentItems) {
        CartItem existingItem = null;
        if (currentItems != null) {
            for (CartItem item : currentItems) {
                if (item.getProduct().getId() != null && item.getProduct().getId() == productId) {
                    existingItem = item;
                    break;
                }
            }
        }

        CartItemRequest request = new CartItemRequest();
        request.setProductId(productId);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            request.setQuantity(newQuantity);
            return cartRepository.updateItem(existingItem.getId(), request);
        } else {
            request.setQuantity(quantity);
            return cartRepository.addItem(request);
        }
    }
}

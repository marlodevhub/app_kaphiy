package com.marlodev.app_android.domain.usecase.cart;

import androidx.lifecycle.LiveData;
import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.utils.Result;
import java.math.BigDecimal;
import java.util.List;

/**
 * Caso de uso inteligente que decide si añadir un nuevo item o actualizar uno existente.
 * Encapsula la lógica de negocio que antes estaba en el ViewModel.
 */
public class AddOrUpdateCartItemUseCase {

    private final AddItemToCartUseCase addItemToCartUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;

    public AddOrUpdateCartItemUseCase(AddItemToCartUseCase addItemToCartUseCase, UpdateCartItemUseCase updateCartItemUseCase) {
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
    }

    public LiveData<Result<Order>> execute(List<CartItem> currentItems, Product product, int quantity) {
        CartItem existingItem = findItemByProductId(currentItems, product.getId());

        if (existingItem != null) {
            // El item ya existe, se actualiza la cantidad y el precio total.
            int newQuantity = existingItem.getQuantity() + quantity;
            existingItem.setQuantity(newQuantity);
            if (existingItem.getUnitPrice() != null) {
                existingItem.setTotalPrice(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            }
            return updateCartItemUseCase.execute(existingItem.getId(), existingItem);
        } else {
            // El item es nuevo, se crea y se añade.
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            newItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            return addItemToCartUseCase.execute(newItem);
        }
    }

    private CartItem findItemByProductId(List<CartItem> items, Long productId) {
        if (items == null || productId == null) return null;

        return items.stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId() != null
                        && item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}

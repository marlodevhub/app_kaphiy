package com.marlodev.app_android.ui.client.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.utils.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClientCartViewModel extends ViewModel {

    private final CartUseCases cartUseCases;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<BigDecimal> totalPrice = new MutableLiveData<>(BigDecimal.ZERO);

    public ClientCartViewModel(CartUseCases cartUseCases) {
        this.cartUseCases = cartUseCases;
        loadCart();
    }

    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getTotalItems() { return totalItems; }
    public LiveData<BigDecimal> getTotalPrice() { return totalPrice; }

    public void loadCart() {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;
        isLoading.setValue(true);
        cartUseCases.getGetCart().execute().observeForever(this::handleCartUpdateResult);
    }

    // This method remains for other parts of the app (e.g., product detail page)
    public LiveData<Result<Order>> addItemToCart(Product product, int quantity) {
        List<CartItem> currentItemsList = cartItems.getValue();
        return cartUseCases.getAddOrUpdateItem().execute(currentItemsList, product, quantity);
    }

    public void updateQuantity(CartItem item, int newQty) {
        isLoading.setValue(true);
        List<CartItem> currentItems = new ArrayList<>(Objects.requireNonNull(cartItems.getValue()));
        CartItem itemToUpdateInBackend = null;

        for (int i = 0; i < currentItems.size(); i++) {
            CartItem currentItem = currentItems.get(i);
            if (Objects.equals(currentItem.getId(), item.getId())) {
                CartItem updatedItem = CartItem.builder()
                        .id(currentItem.getId())
                        .product(currentItem.getProduct())
                        .variant(currentItem.getVariant())
                        .extras(currentItem.getExtras())
                        .quantity(newQty)
                        .unitPrice(currentItem.getUnitPrice())
                        .totalPrice(currentItem.getUnitPrice() != null ? currentItem.getUnitPrice().multiply(BigDecimal.valueOf(newQty)) : null)
                        .build();
                currentItems.set(i, updatedItem);
                itemToUpdateInBackend = updatedItem;
                break;
            }
        }

        cartItems.setValue(currentItems);
        updateTotals(currentItems);

        if (itemToUpdateInBackend != null) {
            cartUseCases.getUpdateCartItem().execute(item.getId(), itemToUpdateInBackend).observeForever(this::handleCartUpdateResult);
        } else {
            isLoading.setValue(false);
        }
    }

    public void deleteItem(CartItem item) {
        isLoading.setValue(true);
        cartUseCases.getDeleteCartItem().execute(item.getId()).observeForever(this::handleCartUpdateResult);
    }

    private void handleCartUpdateResult(Result<Order> result) {
        isLoading.postValue(result.status == Result.Status.LOADING);

        if (result.status == Result.Status.SUCCESS) {
            updateStateFromOrder(result.data);
        } else if (result.status == Result.Status.ERROR) {
            errorMessage.postValue(result.message);
            // Optional: If the operation failed, refresh the cart to get the source of truth from the server
            // refreshCart();
        }
    }

    private void updateStateFromOrder(Order order) {
        if (order == null || order.getItems() == null) {
            cartItems.postValue(Collections.emptyList());
            updateTotals(Collections.emptyList());
            return;
        }

        List<CartItem> items = order.getItems();
        cartItems.postValue(items);
        updateTotals(items);

    }

    private void updateTotals(List<CartItem> items) {
        int count = items.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();

        BigDecimal total = items.stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalItems.postValue(count);
        totalPrice.postValue(total);
    }

    public void refreshCart() {
        loadCart();
    }
}

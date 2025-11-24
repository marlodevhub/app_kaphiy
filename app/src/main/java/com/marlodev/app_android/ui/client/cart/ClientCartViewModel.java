package com.marlodev.app_android.ui.client.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.data.network.model.order.CartItem;
import com.marlodev.app_android.data.network.model.order.CartItemRequest;
import com.marlodev.app_android.data.network.model.order.OrderResponse;
import com.marlodev.app_android.data.repository.CartRepository;
import com.marlodev.app_android.utils.CartNotifier;
import com.marlodev.app_android.utils.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCartViewModel extends ViewModel {

    private final CartRepository repository;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);

    public ClientCartViewModel(@NonNull CartRepository cartRepository) {
        this.repository = cartRepository;
        loadCart();
    }

    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getTotalItems() { return totalItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }

    public void loadCart() {
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            return;
        }
        repository.getCart().observeForever(result -> {
            if (result.status == Result.Status.SUCCESS && result.data != null) {
                List<CartItem> mappedItems = result.data.getItems().stream()
                        .map(itemResponse -> new CartItem(itemResponse.getId(), itemResponse.getProduct(), itemResponse.getQuantity()))
                        .collect(Collectors.toList());
                cartItems.postValue(mappedItems);
                recalcTotals(mappedItems);
            } else if (result.status == Result.Status.ERROR) {
                errorMessage.postValue(result.message);
            }
            isLoading.postValue(result.status == Result.Status.LOADING);
        });
    }

    public LiveData<Result<OrderResponse>> addItemToCart(long productId, int quantity) {
        List<CartItem> currentItems = cartItems.getValue();
        CartItem existingItem = null;
        if (currentItems != null) {
            existingItem = currentItems.stream()
                    .filter(item -> item.getProduct() != null && item.getProduct().getId() != null && item.getProduct().getId() == productId)
                    .findFirst().orElse(null);
        }

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            CartItemRequest request = new CartItemRequest(productId, null, null, newQuantity, null);
            return repository.updateItem(existingItem.getId(), request);
        } else {
            CartItemRequest request = new CartItemRequest(productId, null, null, quantity, null);
            return repository.addItem(request);
        }
    }

    public void updateQuantity(CartItem item, int newQty) {
        item.setQuantity(newQty);
        cartItems.setValue(cartItems.getValue()); // refresca LiveData
        recalcTotals(cartItems.getValue());
        CartNotifier.notifyCartUpdated();
    }

    public void deleteItem(CartItem item) {
        repository.deleteItem(item.getId()).observeForever(result -> {
            if (result.status == Result.Status.SUCCESS && result.data != null) {
                List<CartItem> mappedItems = result.data.getItems().stream()
                        .map(itemResponse -> new CartItem(itemResponse.getId(), itemResponse.getProduct(), itemResponse.getQuantity()))
                        .collect(Collectors.toList());
                cartItems.postValue(mappedItems);
                recalcTotals(mappedItems);
                CartNotifier.notifyCartUpdated();
            } else if (result.status == Result.Status.ERROR) {
                errorMessage.postValue(result.message);
            }
            isLoading.postValue(result.status == Result.Status.LOADING);
        });
    }

    private void recalcTotals(List<CartItem> items) {
        if (items == null) return;
        int count = items.stream().mapToInt(CartItem::getQuantity).sum();
        double total = items.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
                .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                .sum();
        totalItems.postValue(count);
        totalPrice.postValue(total);
    }

    public void checkout() {
        repository.checkout().observeForever(result -> {
            if (result.status == Result.Status.SUCCESS) {
                cartItems.postValue(new ArrayList<>());
                recalcTotals(new ArrayList<>());
                errorMessage.postValue("Compra realizada exitosamente");
                CartNotifier.notifyCartUpdated();
            } else if (result.status == Result.Status.ERROR) {
                errorMessage.postValue(result.message);
            }
            isLoading.postValue(result.status == Result.Status.LOADING);
        });
    }

    public void refreshCart() {
        loadCart();
    }
}

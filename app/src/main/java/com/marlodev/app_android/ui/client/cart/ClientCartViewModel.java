package com.marlodev.app_android.ui.client.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.model.CartItem;
import com.marlodev.app_android.domain.model.Order;
import com.marlodev.app_android.domain.model.Product;
import com.marlodev.app_android.domain.usecase.cart.CartUseCases;
import com.marlodev.app_android.utils.CartNotifier;
import com.marlodev.app_android.utils.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClientCartViewModel extends ViewModel {

    private final CartUseCases cartUseCases;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
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
        cartUseCases.getGetCart().execute().observeForever(this::handleCartResult);
    }

    public LiveData<Result<Order>> addItemToCart(Product product, int quantity) {
        List<CartItem> currentItemsList = cartItems.getValue();
        return cartUseCases.getAddOrUpdateItem().execute(currentItemsList, product, quantity);
    }

    public void updateQuantity(CartItem item, int newQty) {
        item.setQuantity(newQty);
        if (item.getUnitPrice() != null) {
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(newQty)));
        }
        // NOTE: This is still fire-and-forget, may need same pattern if issues arise.
        cartUseCases.getUpdateCartItem().execute(item.getId(), item).observeForever(this::handleCartResult);
        recalcTotals(cartItems.getValue());
        CartNotifier.notifyCartUpdated();
    }

    public void deleteItem(CartItem item) {
        // NOTE: This is still fire-and-forget, may need same pattern if issues arise.
        cartUseCases.getDeleteCartItem().execute(item.getId()).observeForever(this::handleCartResult);
    }

    /**
     * Public method for activities/fragments to call to update the ViewModel's state after an operation.
     * @param order The updated order object from a successful cart operation.
     */
    public void onCartUpdated(Order order) {
        if (order == null) return;
        cartItems.postValue(order.getItems());
        recalcTotals(order.getItems());
        CartNotifier.notifyCartUpdated(); // Notifies other parts of the app, like a badge count.
    }

    /**
     * Handles results from internally observed LiveData (like loadCart).
     */
    private void handleCartResult(Result<Order> result) {
        isLoading.postValue(result.status == Result.Status.LOADING);

        if (result.status == Result.Status.SUCCESS) {
            onCartUpdated(result.data); // Reuse the public update logic
        } else if (result.status == Result.Status.ERROR) {
            errorMessage.postValue(result.message);
        }
    }

    private void recalcTotals(List<CartItem> items) {
        if (items == null) return;

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

package com.marlodev.app_android.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.dto.order.CartItem;
import com.marlodev.app_android.dto.product.ProductResponse;
import com.marlodev.app_android.model.order.CartItemResponse;
import com.marlodev.app_android.dto.order.OrderResponse;
import com.marlodev.app_android.repository.CartRepository;
import com.marlodev.app_android.dto.order.network.ApiClient;
import com.marlodev.app_android.dto.order.network.order.CartApi;
import com.marlodev.app_android.utils.CartNotifier;

import java.util.ArrayList;
import java.util.List;

public class ClientCartVM extends AndroidViewModel {

    private final CartRepository repository;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);

    public ClientCartVM(@NonNull Application app) {
        super(app);
        CartApi api = ApiClient.getClient(app).create(CartApi.class);
        repository = new CartRepository(api);
        loadCart();
    }

    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getTotalItems() { return totalItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }

    /** Carga el carrito desde el backend */
    public void loadCart() {
        isLoading.setValue(true);
        repository.getCart(new CartRepository.SimpleCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                List<CartItem> list = new ArrayList<>();
                if (result != null && result.getItems() != null) {

                    for (CartItemResponse item : result.getItems()) {
                        ProductResponse product = item.getProduct();
                        int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                        Long cartItemId = item.getId(); // ← ID del item en el carrito
                        list.add(new CartItem(cartItemId, product, qty)); // ahora son 3 argumentos
                    }
                }
                cartItems.postValue(list);
                recalcTotals(list);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    /** Actualiza cantidad de un item */
    public void updateQuantity(CartItem item, int newQty) {
        item.setQuantity(newQty);
        cartItems.setValue(cartItems.getValue()); // refresca LiveData
        recalcTotals(cartItems.getValue());
        CartNotifier.notifyCartUpdated(); // 🔹 notificar cambio
    }

    /** Elimina un item del carrito */




    public void deleteItem(CartItem item) {
        repository.deleteItem(item.getId(), new CartRepository.SimpleCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {

                List<CartItem> updatedList = new ArrayList<>();

                if (result != null && result.getItems() != null) {
                    for (CartItemResponse resp : result.getItems()) {
                        ProductResponse product = resp.getProduct();
                        int qty = resp.getQuantity() != null ? resp.getQuantity() : 1;
                        updatedList.add(new CartItem(resp.getId(), product, qty)); // ✔️ 3 argumentos
                    }
                }

                cartItems.postValue(updatedList);
                recalcTotals(updatedList);

                CartNotifier.notifyCartUpdated();
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }


    /** Calcula totales */
    private void recalcTotals(List<CartItem> items) {
        if (items == null) return;
        int count = 0;
        double total = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
            if (item.getProduct().getPrice() != null)
                total += item.getProduct().getPrice().doubleValue() * item.getQuantity();
        }
        totalItems.postValue(count);
        totalPrice.postValue(total);
    }

    /** Checkout */
    public void checkout() {
        isLoading.setValue(true);
        repository.checkout(new CartRepository.SimpleCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                cartItems.postValue(new ArrayList<>());
                recalcTotals(new ArrayList<>());
                isLoading.postValue(false);
                errorMessage.postValue("Compra realizada exitosamente");
                CartNotifier.notifyCartUpdated(); // 🔹 notificar cambio
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    /** Recargar carrito desde cualquier lugar */
    public void refreshCart() {
        loadCart();
    }
}
